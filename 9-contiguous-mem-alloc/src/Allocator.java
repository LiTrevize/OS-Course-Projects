import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Seg {
    public int pid, base;

    public Seg(int pid, int base) {
        this.pid = pid;
        this.base = base;
    }
}

public class Allocator {
    private List<Seg> mem = new ArrayList<>();

    public Allocator(int size) {
        // -1 marks available memory fragment
        mem.add(new Seg(-1, 0));
        // a virtual proc mark the end of memory space
        mem.add(new Seg(-2, size));
    }

    private int size(int i) {
        return mem.get(i + 1).base - mem.get(i).base;
    }

    private int size(Seg p) {
        int i = mem.indexOf(p);
        if (i < 0) return -1;
        return mem.get(i + 1).base - mem.get(i).base;
    }

    public int request(int pid, int size, char flag) {
        if (flag == 'F') {
            for (int i = 0; i < mem.size(); i++) {
                if (mem.get(i).pid == -1 && size(i) > size) {
                    int base = mem.get(i).base;
                    mem.get(i).base += size;
                    mem.add(i, new Seg(pid, base));
                    return 0;
                }
            }
        }
        // Best-fit or Worst-fit
        List<Seg> list = new ArrayList<Seg>();
        for (Seg p : mem) {
            if (p.pid == -1)
                list.add(p);
        }
        if (flag == 'B')
            list.sort(new Comparator<Seg>() {
                @Override
                public int compare(Seg a, Seg b) {
                    return size(a) - size(b);
                }
            });
        else if (flag == 'W')
            list.sort(new Comparator<Seg>() {
                @Override
                public int compare(Seg a, Seg b) {
                    return size(b) - size(a);
                }
            });
        // search for available
        for (Seg p : list) {
            if (size(p) >= size) {
                int base = p.base;
                p.base += size;
                mem.add(mem.indexOf(p), new Seg(pid, base));
                return 0;
            }
        }
        // no big enough fragment
        System.err.println("Cannot find available contiguous memory space.");
        return -1;
    }

    private void combine() {
        int i = 1;
        // not consider the last virtual process
        while (i < mem.size() - 1) {
            if (mem.get(i).pid == -1 && mem.get(i - 1).pid == -1) {
                mem.remove(i);
            } else i++;
        }
    }

    public void release(int pid) {
        for (int i = 0; i < mem.size(); i++) {
            if (mem.get(i).pid == pid)
                mem.get(i).pid = -1;
        }
        // combine unused memory
        combine();
    }

    public void compact() {
        int i = 1;
        // not consider the last virtual process
        while (i < mem.size() - 1) {
            // swap proc and unused fragment
            if (mem.get(i).pid >= 0 && mem.get(i - 1).pid == -1) {
                int newbase = mem.get(i+1).base-size(i-1);
                mem.get(i-1).pid = mem.get(i).pid;
                mem.get(i).pid = -1;
                mem.get(i).base = newbase;
            // combine two unused
            } else if (mem.get(i).pid == -1 && mem.get(i - 1).pid == -1){
                mem.remove(i);
                continue;
            }
            i++;
        }

    }

    public void status() {
        for (int i = 0; i < mem.size() - 1; i++) {
            int beg = mem.get(i).base;
            int pid = mem.get(i).pid;
            int end = mem.get(i + 1).base - 1;
            if (pid >= 0)
                System.out.println("Addresses [" + beg + ":" + end + "] Process P" + pid);
            else
                System.out.println("Addresses [" + beg + ":" + end + "] Unused");
        }
    }
}
