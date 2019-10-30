import java.util.concurrent.*;
import java.util.Arrays;

public class MergeSortTask<T extends Comparable<T>> extends RecursiveAction {
    private int beg;
    private int end;
    private T[] arr;

    public MergeSortTask(T[] arr, int beg, int end) {
        this.arr = arr;
        this.beg = beg;
        this.end = end;
    }

    public void merge(int beg, int mid, int end) {
        int i, j, k;
        int im = mid - beg;
        int jm = end - mid + 1;
        // new T[] will cause an error!
        // T[] a = new T[mid-beg]
        T[] a = Arrays.copyOfRange(arr, beg, mid);
        T[] b = Arrays.copyOfRange(arr, mid, end+1);
        i = j = 0;
        k = beg;
        while (i < im && j < jm) {
            if (a[i].compareTo(b[j]) <= 0) {
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
            if (arr[beg].compareTo(arr[end]) > 0) {
                T tmp = arr[beg];
                arr[beg] = arr[end];
                arr[end] = tmp;
            }
            return;
        }
        if (beg == end) return;
        MergeSortTask leftTask = new MergeSortTask<T>(arr, beg, (beg + end) / 2);
        MergeSortTask rightTask = new MergeSortTask<T>(arr, (beg + end) / 2 + 1, end);

        leftTask.fork();
        rightTask.fork();

        leftTask.join();
        rightTask.join();

        merge(beg, (beg + end) / 2 + 1, end);

        return;
    }

}
