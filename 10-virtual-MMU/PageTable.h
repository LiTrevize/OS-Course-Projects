#ifndef INC_10_VIRTUAL_MMU_PAGETABLE_H
#define INC_10_VIRTUAL_MMU_PAGETABLE_H

class PageTable {
private:
    unsigned char table[1 << 8];
    bool valid[1 << 8];
    int curidx;
    bool full;
public:
    PageTable() {
        curidx = 0;
        full = false;
        for (int i = 0; i < (1 << 8); ++i) {
            valid[i] = false;
        }
    }

    bool is_valid(unsigned char pn) const {
        return valid[pn];
    }

    unsigned char get(unsigned char pn){
        return table[pn];
    }

    unsigned char allocate(unsigned char pn) {
        unsigned char fn;
        if (!full) {
            fn = curidx++;
            table[pn] = fn;
            valid[pn] = true;
            if (curidx == (1 << 8)) full = true;
            return fn;
        }
        // full. page replacement through FIFO
        curidx = curidx % (1 << 8);
        int victim_pn = curidx++;
        fn = table[victim_pn];
        valid[victim_pn] = false;
        table[pn] = fn;
        valid[pn] = true;
        return fn;
    }
};

#endif //INC_10_VIRTUAL_MMU_PAGETABLE_H
