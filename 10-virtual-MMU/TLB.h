#ifndef INC_10_VIRTUAL_MMU_TLB_H
#define INC_10_VIRTUAL_MMU_TLB_H

#include <queue>

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
    std::queue<int> q;
    int curlen;
public:
    TLB() { curlen = 0; }

    bool contains(unsigned char pn, unsigned char &fn) {
        for (int i = 0; i < curlen; i++)
            if (cache[i].pn == pn) {
                fn = cache[i].fn;
                return true;
            }
        return false;
    }

    void add(unsigned char pn, unsigned char fn) {
        if (curlen < 16) {
            q.push(curlen);
            cache[curlen++] = Entry(pn, fn);
            return;
        }

        // need to replace through FIFO
        int victim_id = q.front();
        q.pop();
        cache[victim_id] = Entry(pn, fn);
        q.push(victim_id);
    }
};

#endif //INC_10_VIRTUAL_MMU_TLB_H
