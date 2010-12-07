#include "cvs.h"

using namespace std;

// G can be directed
int dijkstra(const mimii &G, int source, int sink, mii &prev)
{
    mii dist;

    priority_queue<pii, vector<pii>, greater<pii> > pq;
    dist[source] = 0;
    prev[source] = -1;
    pq.push(pii(dist[source], source));

    while (!pq.empty())
    {
        pii uq = pq.top(); pq.pop();
        const int &u = uq.second;
        if (dist[u] < uq.first)
            continue;

        if (u == sink) 
            return uq.first;

        const mii &Gu = G.find(u)->second;
        for (mii::const_iterator vt = Gu.begin(); vt != Gu.end(); vt++)
        {
            const int &v = vt->first;
            // int dv = dist[u] + G[u][v];
            int dv = uq.first + vt->second;
            if (dist.count(v) && dist[v] <= dv)
                continue;
            dist[v] = dv;
            prev[v] = u;
            pq.push(pii(dist[v], v));
        }
    }
    return -1;
}

mimii CVSOpt::build_graph_x(int x, mii &T0inv)
{
    int Gix = 0;
    mimii G; // directed
    mii T1, T0; // T -> G.nodes
    // T0 pre-call, post-transform
    // T1 post-call, pre-transform

    int source = Gix++;
    int sink = Gix++;
    T1[init_type] = source; // node for init_type doubles as source node

    for (int m = 0; m < nM; m++)
    {
        mii &CVmx = CV[m][x]; // T -> Z
        T0 = mii();
        foreach(pii tt, CVmx)
        {
            T0[tt.first] = Gix++;
            T0inv[T0[tt.first]] = tt.first;
        }

        foreach(pii tt1, T1)
        foreach(pii tt0, T0)
            G[tt1.second][tt0.second] = CT[tt1.first][tt0.first];

        T1 = mii();
        foreach(pii tt, CVmx)
            T1[tt.first] = Gix++;

        foreach(pii tt, CVmx)
            G[T0[tt.first]][T1[tt.first]] = tt.second;
    }

    foreach(pii tt1, T1)
        G[tt1.second][sink] = 0;

    return G;
}

void CVSOpt::solve()
{
    Acal = vmii(nM);
    for (int x = 0; x < nX; x++)
    {
        mii T0inv; // G.nodes -> T
        mimii G = build_graph_x(x, T0inv);
        mii Gprev; // G.nodes -> G.nodes
        int cost = dijkstra(G, 0, 1, Gprev);

        list<int> path;
        for (int v = 1; v != -1; v = Gprev[v])
            path.push_front(v);
        
        vi mt; // M -> T
        foreach(int v, path)
            if (T0inv.count(v))
                mt.push_back(T0inv[v]);

        for (int i = 0; i < mt.size(); i++)
            Acal[i][x] = mt[i];
    }
}

// make sure T[x][x] = 0
CVSOpt::CVSOpt(istream &in)
{
    in >> nX >> nT >> nM;

    CV = vmimii(nM);

    for (int m = 0; m < nM; m++)
    {
        int nl;
        in >> nl;
        for (int l = 0; l < nl; l++)
        {
            int x, t, c;
            cin >> x >> t >> c;
            CV[m][x][t] = c;
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

int main(int argc, char **argv)
{
    CVSOpt opt(cin);
    opt.solve();
    return 0;
}

