import java.util.concurrent.*;

public class MergeSortTask extends RecursiveAction {
    private int beg;
    private int end;
    public int[] arr;

    public MergeSortTask(int[] arr, int beg, int end) {
        this.beg = beg;
        this.end = end;
        this.arr = arr;
    }

    public void merge(int beg, int mid, int end) {
        int i, j, k;
        int im = mid - beg;
        int jm = end - mid + 1;
        int[] a = new int[im];
        int[] b = new int[jm];
        for (i = 0; i < im; ++i)
            a[i] = arr[beg + i];
        for (j = 0; j < jm; ++j)
            b[j] = arr[beg + i + j];
        i = j = 0;
        k = beg;
        while (i < im && j < jm) {
            if (a[i] <= b[j]) {
                arr[k] = a[i];
                i++;
            } else {
                arr[k] = b[j];
                j++;
            }
            k++;
        }
        if (i == im) {
            while (j < jm)
                arr[k++] = b[j++];
        } else {
            while (i < im)
                arr[k++] = a[i++];
        }
        return;
    }

    @Override
    protected void compute() {
        if (beg == end - 1) {
            if (arr[beg] > arr[end]) {
                int tmp = arr[beg];
                arr[beg] = arr[end];
                arr[end] = tmp;
            }
            return;
        }
        if (beg == end) return;
        MergeSortTask leftTask = new MergeSortTask(arr, beg, (beg + end) / 2);
        MergeSortTask rightTask = new MergeSortTask(arr, (beg + end) / 2 + 1, end);

        leftTask.fork();
        rightTask.fork();

        leftTask.join();
        rightTask.join();

        merge(beg, (beg + end) / 2 + 1, end);

        return;
    }

}
