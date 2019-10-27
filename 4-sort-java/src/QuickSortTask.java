import java.util.concurrent.*;

public class QuickSortTask extends RecursiveAction {
    private int beg;
    private int end;
    public int[] arr;

    public QuickSortTask(int[] arr, int beg, int end) {
        this.arr = arr;
        this.beg = beg;
        this.end = end;
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
        int left = beg, right = end, mid = (beg + end) / 2, pivot;
        // choose first one as pivot
        pivot = arr[beg];
        //
        while (left < right) {
            while (left < right && arr[right] > pivot)
                right--;
            if (left < right) {
                arr[left] = arr[right];
                left++;
            }
            while (left < right && arr[left] < pivot)
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