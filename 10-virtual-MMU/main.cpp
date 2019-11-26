#include <iostream>
#include <fstream>
#include <random>
#include <algorithm>
#include "TLB.h"

using namespace std;

char phy_mem[1 << 16];
bool frame_loaded[1 << 8];
TLB tlb;
ifstream mem;
int counts, tlb_hit, page_fault;

void generate_page_table() {
    char a[256];
    for (int i = 0; i < 256; i++)
        a[i] = i;
    shuffle(a, a + 256, std::default_random_engine());
    ofstream pt("page_table.bin", ios::out | ios::binary);
    pt.write(a, 256);
    pt.close();
}

void load_frame(int page_num, int frame_num) {
    mem.seekg((page_num << 8) * sizeof(char));
    mem.read(phy_mem + (frame_num << 8), 1 << 8);
    frame_loaded[frame_num] = true;
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
        frame_num = page_table(page_num);
        tlb.add(page_num, frame_num);
    }

    return (frame_num << 8) + offset;
}

int load(int log_addr, int &phy_addr) {
    unsigned char pn, fn, offset;
    counts++;
    // translate
    phy_addr = translate(log_addr, pn, fn, offset);

//    cout<<(int)pn<<endl;

    // if frame not loaded
    if (!frame_loaded[fn])
        load_frame(pn, fn);

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

    // no frame is loaded initially
    for (int i = 0; i < (1 << 8); i++)
        frame_loaded[i] = false;

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
    fin.close();
    mem.close();
    return 0;
}