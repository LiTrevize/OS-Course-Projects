#ifndef INC_10_VIRTUAL_MMU_PAGETABLE_H
#define INC_10_VIRTUAL_MMU_PAGETABLE_H

#include "TLB.h"

class PageTable {
private:
    unsigned char table[1 << 8];
    bool valid[1 << 8];
    int phy_mem_size;
    int curidx;
    bool full;
public:
    PageTable(int size) {
        curidx = 0;
        full = false;
        phy_mem_size = size;
        for (int i = 0; i < (1 << 8); ++i) {
            valid[i] = false;
        }
    }

    bool is_valid(unsigned char pn) const {
        return valid[pn];
    }

    unsigned char get_fn(unsigned char pn) {
        return table[pn];
    }

    unsigned char get_pn(unsigned char fn) {
        int last;
        if (full) last = 1 << 8;
        else last = curidx;
        for (int i = 0; i < last; i++) {
            if (valid[i] && table[i] == fn)
                return i;
        }
    }

    // desired page is not invalid
    void check_tlb(TLB &tlb) {
        curidx %= phy_mem_size;
        while (tlb.contains_fn(curidx))
            curidx = (curidx + 1) % phy_mem_size;
    }

    unsigned char allocate(unsigned char pn) {
        unsigned char fn;
        if (!full) {
            fn = curidx++;
            table[pn] = fn;
            valid[pn] = true;
            if (curidx == phy_mem_size) full = true;
            return fn;
        }
        if (curidx == phy_mem_size >> 1);
        // full. page replacement through FIFO
        curidx = curidx % phy_mem_size;
        fn = curidx++;
        int victim_pn = get_pn(fn);
        valid[victim_pn] = false;
        table[pn] = fn;
        valid[pn] = true;
        return fn;
    }
};

#endif //INC_10_VIRTUAL_MMU_PAGETABLE_H
