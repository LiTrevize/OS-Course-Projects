#include <iostream>
#include <fstream>
#include <random>
#include <algorithm>
#include "TLB.h"
#include "PageTable.h"

using namespace std;

char phy_mem[1 << 16];
//bool frame_loaded[1 << 7];
//unsigned char page_in_frame[1 << 7];
TLB tlb;
PageTable pgt;
ifstream mem;
int counts, tlb_hit, page_fault;


void load_page(int page_num, int frame_num) {
    mem.seekg((page_num << 8) * sizeof(char));
    mem.read(phy_mem + (frame_num << 8), 1 << 8);
}

unsigned char page_table(unsigned char page_num) {
    ifstream pt("page_table.bin", ios::binary);
    pt.seekg(page_num * sizeof(char));
    char frame_num;
    pt.read(&frame_num, 1);
    pt.close();
    return frame_num;
}

int translate(int log_addr, unsigned char &page_num, unsigned char &frame_num, unsigned char &offset) {
    // parse logical address
    offset = log_addr & ((1 << 8) - 1);
    log_addr >>= 8;
    page_num = log_addr & ((1 << 8) - 1);

    // translate through TLB
    if (tlb.contains(page_num, frame_num)) {
        tlb_hit++;
    } else {
        // translate through page table
        if (pgt.is_valid(page_num))
            frame_num=pgt.get(page_num);
        else {
            frame_num=pgt.allocate(page_num);
            load_page(page_num,frame_num);
            tlb.add(page_num, frame_num);
        }
    }

    return (frame_num << 8) + offset;
}

int load(int log_addr, int &phy_addr) {
    unsigned char pn, fn, offset;
    counts++;
    // translate
    phy_addr = translate(log_addr, pn, fn, offset);

    return phy_mem[phy_addr];
}

int main() {
//    generate_page_table();
//    return 0;
    int la, offset, pa, count = 0;
    unsigned char pn, fn;
    char byte;
    counts = tlb_hit = page_fault = 0;
    ifstream fin("addresses.txt", ios::in);
    mem.open("BACKING_STORE.bin", ios::binary);
    char line[10];


    while (!fin.eof()) {
        // get logical address
        fin >> line;
        la = atoi(line);
        // load data from virtual address
        byte = load(la, pa);
        cout << "Virtual address: " << la << " Physical address: " << pa << " Value: " << (int) byte << endl;

//        if (count++ == 30) break;
    }
    cout << "TLB hit: " << tlb_hit << endl;
    cout << "Page-fault: " << page_fault << endl;
    fin.close();
    mem.close();
    return 0;
}