#include "cm.h"

using namespace std;

mimii graph_reverse(mimii &G)
{
    mimii H;
    foreach (pimii p, G)
        foreach(pii pu, G[p.first])
            H[p.first][pu.first] = pu.second;
    return H;
}

int CMOpt::get_node(int z, const int& m, const int &aix)
{
    if (aix == -1)
        assert(z == 1 && m == -1);
    switch (z)
    {
        //pre call, post transform
        case 0:
            return 2 + nA[m] + aix;
        //post call, pre transform
        case 1:
            if (aix == -1)
                return 0;
            else
                return 2 + nA[nM] + nA[m] + aix;
    }
}

cm_graph CMOpt::build_graph()
{
    mii init_a;
    for (int x = 0; x < nX; x++)
        init_a[x] = init_type;

    cm_graph G; // directed

    int source = 0;
    int sink = 1;

    for (int x = 0; x < nX; x++)
    {
        si xv; // M -> {0, 1}
        for (int m = 0; m < nM; m++)
            if (MX[m].count(x))
                xv.insert(m);
        xv.insert(-1); // init_type post-call node

        // add nodes

        // add call edges
        foreach(int m, xv)
        {
            if (m == -1)
                continue;
            foreach(pii p, CM[m])
            {
                int v0 = get_node(0, m, p.first);
                int v1 = get_node(1, m, p.first);
                G.cap[v0][v1] = nX;
                G.cost[v0][v1] = p.second;
            }
        }

        // add transform edges
        for (si::iterator it = xv.begin(); it != xv.end(); it++)
        {
            si::iterator jt = it;
            jt++;
            if (jt == xv.end())
                break;

            const int &m0 = *it;
            const int &m1 = *jt;

            mii CMm0;
            if (m0 == -1)
                CMm0[-1] = 0;
            else
                CMm0 = CM[m0];
            
            foreach(pii p0, CMm0)
            foreach(pii p1, CM[m1])
            {
                const int &a0ix = p0.first;
                const int &a1ix = p1.first;
                int t0 = AM[m0][a0ix][x];
                int t1 = AM[m1][a1ix][x];

                int u = get_node(1, m0, a0ix);
                int v = get_node(0, m1, a1ix);
                G.cost[u][v] += CT[t0][t1];
                G.cap[u][v] += 1;
            }
        }
    }

    {
        int m = CM.size() - 1;
        foreach(pii p, CM[m])
        {
            int u = get_node(1, m, p.first);
            G.cap[u][sink] = nX;
            G.cost[u][sink] = 0;
        }
    }

    return G;
}

int CMOpt::get_sos_var(mpiii &Einv, const int &m, const int &aix)
{
    int u = get_node(0, m, aix);
    int v = get_node(1, m, aix);
    return Einv[pii(u, v)];
}

void CMOpt::find_opt()
{
    cm_graph G = build_graph();

    mpiii Einv;
    lprec *lp = build_lp(G, Einv);
    vi b = solve_lp(lp);

    vsi MAix; // M Aix -> {0, 1}
    for (int m = 0; m < nM; m++)
        for (int aix = 0; aix < nA[m]; aix++)
        {
            int eix = get_sos_var(Einv, m, aix);
            assert(b[eix] == 0 || b[eix] == nX);
            if (b[eix])
                MAix[m].insert(aix);
        }

    Acal = vmii(nM);
    for (int m = 0; m < nM; m++)
    {
        const si &s = MAix[m];

        assert(s.size() == 1);
        const int &aix = *s.begin();
        Acal[m] = AM[m][aix];
    }

    delete_lp(lp);
}

lprec *CMOpt::build_lp(cm_graph &G, mpiii &Einv)
{
    Einv = mpiii(); // E -> Eix
    mipii E; // Eix -> E

    foreach (pimii pu, G.cost)
        foreach (pii pv, pu.second)
        {
            pii e = pii(pu.first, pv.first);
            int eix = 1 + Einv.size();
            Einv[e] = eix;
            E[eix] = e;
        }

    lprec *lp = make_lp(0, E.size());
    assert(lp);

    set_add_rowmode(lp, 1);

    mimii Gr = graph_reverse(G.cost);
    mimii &Gf = G.cost;

    /* integer lp */
    foreach(pipii p, E)
        set_int(lp, p.first, 1);

    /* conservation of flow */
    foreach (pimii p, G.cost)
    {
        int u = p.first;
        int n = Gr[u].size() + Gr[u].size();
        double *a = new double[n];
        int *b = new int[n];

        int i = 0;
        foreach (pii p, Gr[u])
        {
            int &v = p.first;
            a[i] = -1;
            b[i] = Einv[pii(v, u)];
            i += 1;
        }
        foreach (pii p, Gf[u])
        {
            int &v = p.first;
            a[i] = 1;
            b[i] = Einv[pii(u, v)];
            i += 1;
        }

        int d;
        if (u == 0)
            d = nX;
        else if (u == 1)
            d = -nX;
        else
            d = 0;

        add_constraintex(lp, n, a, b, EQ, d);

        assert(i == n);
        delete[] a;
        delete[] b;
    }

    /* capacity constraints */
    foreach(pipii p, E)
    {
        int &eix = p.first;
        pii &e = p.second;

        const int n = 1;
        double a[n];
        int b[n];
        a[0] = 1;
        b[0] = eix;
        int cap = G.cap[e.first][e.second];
        add_constraintex(lp, n, a, b, LE, cap);
    }

    /* sos */
    for (int m = 0; m < nM; m++)
    {
        int *s = new int[nA[m]];
        for (int aix = 0; aix < nA[m]; aix++)
            s[aix] = get_sos_var(Einv, m, aix);

        char sos_str[10];
        snprintf(sos_str, 10, "%m%02d", m);
        add_SOS(lp, sos_str, 1, 1, nA[m], s, NULL);
    }

    set_add_rowmode(lp, 0);

    /* objective function */
    {
        int n = E.size();
        double *a = new double[n + 1];

        foreach(pipii p, E)
        {
            int &eix = p.first;
            pii &e = p.second;

            a[eix] = G.cost[e.first][e.second];
        }

        set_obj_fn(lp, a);

        delete[] a;
    }

    return lp;
}

vi CMOpt::solve_lp(lprec *lp)
{
    set_minim(lp);

    write_LP(lp, stdout);
    set_verbose(lp, IMPORTANT);

    int ret = solve(lp);
    assert (ret == OPTIMAL);

    int n = get_Ncolumns(lp);
    double *b = new double[n];
    get_variables(lp, b);

    vi vb(n);
    for (int i = 0; i < n; i++)
    {
        double f = round(b[i]);
        assert(abs(f - b[i]) < 1e-7);
        vb[i] = (int)(b[i] + 0.5);
    }
    return vb;
}

// b: M -> aix
int CMOpt::next_b(vi &b)
{
    b[0] += 1;
    for (int m = 0; m < nM; m++)
    {
        if (b[m] == nA[m])
        {
            if (m == nM - 1)
                return 0;
            b[m] = 0;
            b[m + 1] += 1;
        }
        else
            break;
    }
    return 1;
}

int CMOpt::calc_cost(const vi &b)
{
    int cost = 0;
    for (int x = 0; x < nX; x++)
    {
        si xv; // M -> {0, 1}
        for (int m = 0; m < nM; m++)
            if (MX[m].count(x))
                xv.insert(m);
        xv.insert(-1); // init_type post-call node

        foreach (int m, xv)
            cost += CM[m][b[m]];

        for (si::iterator it = xv.begin(); it != xv.end(); it++)
        {
            si::iterator jt = it;
            jt++;
            if (jt == xv.end())
                break;

            const int &m0 = *it;
            const int &m1 = *jt;

            int t0 = m0 == -1 ? init_type : b[m0];
            int t1 = b[m1];
            
            cost += CT[t0][t1];
        }
    }

    return cost;
}

void CMOpt::brute_force()
{
    int min_cost = INT_MAX;
    vi min_b;

    vi b(nM);
    do
    {
        int cost = calc_cost(b);
        if (cost < min_cost)
        {
            min_cost = cost;
            min_b = b;
        }
    } while (next_b(b));

    Acal = vmii(nM);
    for (int m = 0; m < nM; m++)
        Acal[m] = AM[m][min_b[m]];
}

// make sure T[x][x] = 0, tri ieq
CMOpt::CMOpt(istream &in)
{
    in >> nX >> nT >> nM;

    CM = vmii(nM);
    MX = vsi(nM);

    for (int m = 0; m < nM; m++)
    {
        int nV;
        cin >> nV;
        for (int v = 0; v < nV; v++)
        {
            int x;
            cin >> x;
            MX[m].insert(x);
        }

        int nl;
        in >> nl;
        for (int l = 0; l < nl; l++)
        {
            mii a;
            foreach(int x, MX[m])
            {
                int t;
                cin >> t;
                a[x] = t;
            }
            int c;
            cin >> c;

            int aix = nl;
            AM[m][aix] = a;
            AMinv[m][a] = aix;
            CM[m][aix] = c;
        }
    }

    int nl;
    in >> nl;
    for (int l = 0; l < nl; l++)
    {
        int t1, t2, c;
        cin >> t1 >> t2 >> c;
        CT[t1][t2] = c;
    }
}

void CMOpt::setup()
{
    nA = vi(nM);
    for (int m = 0; m < nM; m++)
        nA[m] = CM[m].size();

    nAsum = vi(nM + 1);
    int sum = 0;
    for (int m = 0; m <= nM; m++)
    {
        nAsum[m] = sum;
        if (m != nM)
            sum += nA[m];
    }
}

int main(int argc, char **argv)
{
    CMOpt opt(cin);
    int brute_force = 0;
    if (brute_force)
        opt.brute_force();
    else
        opt.find_opt();
    return 0;
}

