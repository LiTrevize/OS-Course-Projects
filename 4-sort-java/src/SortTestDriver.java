import java.util.concurrent.ForkJoinPool;

public class SortTestDriver {
    public static void main(String[] args) {

        ForkJoinPool pool = new ForkJoinPool();
        int n = 16;
        Integer[] arr = new Integer[n];
        String[] arr2 = new String[n];

        java.util.Random rand = new java.util.Random();
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(2 * n);
            arr2[i] = "" + str.charAt(rand.nextInt(62));
        }

        System.out.println("Raw array:");
        for (int i = 0; i < n; i++)
            System.out.print(arr[i] + " ");
        System.out.println();

        // use fork-join parallelism to merge sort
        MergeSortTask task = new MergeSortTask(arr, 0, n - 1);
        System.out.println("Merge-Sort:");
        pool.invoke(task);

        for (int i = 0; i < n; i++)
            System.out.print(arr[i] + " ");
        System.out.println();

        System.out.println("Raw array:");
        for (int i = 0; i < n; i++)
            System.out.print(arr2[i] + " ");
        System.out.println();

        // use fork-join parallelism to quick sort
        QuickSortTask task2 = new QuickSortTask(arr2, 0, n - 1);
        System.out.println("Quick-Sort:");
        pool.invoke(task2);
        for (int i = 0; i < n; i++)
            System.out.print(arr2[i] + " ");
        System.out.println();
    }
}
