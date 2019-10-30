import java.util.concurrent.*;
import java.util.Arrays;

public class QuickSortTask<T extends Comparable<T>> extends RecursiveAction {
    private int beg;
    private int end;
    private T[] arr;

    public QuickSortTask(T[] arr, int beg, int end) {
        this.arr = arr;
        this.beg = beg;
        this.end = end;
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
        if (beg > end) return;
        int left = beg, right = end, mid = (beg + end) / 2;
        T pivot;
        // choose first one as pivot
        pivot = arr[beg];
        // select and rearrange
        while (left < right) {
            while (left < right && arr[right].compareTo(pivot) > 0)
                right--;
            if (left < right) {
                arr[left] = arr[right];
                left++;
            }
            while (left < right && arr[left].compareTo(pivot) < 0)
                left++;
            if (left < right) {
                arr[right] = arr[left];
                right--;
            }
        }
        arr[left] = pivot;

        QuickSortTask leftTask = new QuickSortTask(arr, beg, left - 1);
        QuickSortTask rightTask = new QuickSortTask(arr, left + 1, end);

        leftTask.fork();
        rightTask.fork();

        leftTask.join();
        rightTask.join();

        return;
    }

}
