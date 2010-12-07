#ifndef CS706_CM_H
#define CS706_CM_H

#include "common.h"
#include <lpsolve/lp_lib.h>

struct cm_graph
{
    mimii cap;
    mimii cost;
};

class CMOpt
{
    public:
    vmii CM; // M Aix -> Z
    mimii CT; // T T -> Z
    vsi MX; // M X -> {0 1}
    int nX, nT, nM;
    static const int init_type = 0;

    vmii Acal; // M X -> T
    vmimii AM; // M Aix -> A
    vmmiii AMinv; // M A -> Aix
    vi nA; // M -> Z
    vi nAsum; // M -> Z
    // nAsum[0] == 0
    // nAsum[1] == nA[0]
    // nAsum[2] == nA[0] + nA[1]
    // ...
    // nAsum[nM]

    CMOpt(std::istream&);
    void brute_force();
    int calc_cost(const vi &b);
    void find_opt();
    void setup();
    vi solve_lp(lprec *lp);
    int next_b(vi &b);
    int get_node(int z, const int &m, const int &aix);
    int get_sos_var(mpiii &Einv, const int &m, const int &aix);
    private:
    cm_graph build_graph();
    lprec *build_lp(cm_graph &G, mpiii &Einv);
};

#endif /* CS706_CM_H */
