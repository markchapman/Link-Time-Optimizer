#ifndef CS706_CVS_H
#define CS706_CVS_H

#include "common.h"

class CVSOpt
{
    public:
    vmimii CV; // M X T -> Z
    mimii CT; // T T -> Z
    int nX, nT, nM;
    static const int init_type = 0;

    vmii Acal; // M X -> T

    CVSOpt(std::istream&);
    void solve();
    private:
    mimii build_graph_x(int x, mii &T0inv);
};

#endif /* CS706_CVS_H */
