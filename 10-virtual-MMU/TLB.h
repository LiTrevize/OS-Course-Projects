#ifndef INC_10_VIRTUAL_MMU_TLB_H
#define INC_10_VIRTUAL_MMU_TLB_H

struct Entry {
    unsigned char pn, fn;

    Entry(unsigned char p = 0, unsigned char f = 0) {
        pn = p;
        fn = f;
    }
};

class TLB {
private:
    Entry cache[16];
    int curidx;
    bool full;
public:
    TLB() {
        curidx = 0;
        full = false;
    }

    bool contains(unsigned char pn, unsigned char &fn) {
        int last;
        if (full) last = 16;
        else last = curidx;
        for (int i = 0; i < last; i++)
            if (cache[i].pn == pn) {
                fn = cache[i].fn;
                return true;
            }
        return false;
    }

    bool contains_fn(unsigned char fn) {
        int last;
        if (full) last = 16;
        else last = curidx;
        for (int i = 0; i < last; i++)
            if (cache[i].fn == fn) {
                return true;
            }
        return false;
    }

    void add(unsigned char pn, unsigned char fn) {
        if (!full) {
            cache[curidx++] = Entry(pn, fn);
            if (curidx == 16) full = true;
            return;
        }

        // need to replace through FIFO
        curidx = curidx % 16;
        int victim_id = curidx++;
        cache[victim_id] = Entry(pn, fn);
    }
};

#endif //INC_10_VIRTUAL_MMU_TLB_H
