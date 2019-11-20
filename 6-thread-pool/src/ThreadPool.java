import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple thread pool API.
 * <p>
 * Tasks that wish to get run by the thread pool must implement the
 * java.lang.Runnable interface.
 */

class Worker implements Runnable {
    private Queue<Runnable> waitQueue;
    private Lock lock;
    private Runnable task = null;

    public Worker(Queue<Runnable> waitQueue, Lock lock) {
        this.waitQueue = waitQueue;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();
            if (!waitQueue.isEmpty())
                task = waitQueue.poll();
            lock.unlock();
            if (!Thread.currentThread().isInterrupted() && task != null)
                task.run();
            task = null;
        }
    }
}


public class ThreadPool {
    private Thread[] workers;
    private Queue<Runnable> waitQueue;
    private int size;
    private Lock lock;


    /**
     * Create a default size thread pool.
     */
    public ThreadPool() {
        size = 4;
        workers = new Thread[size];
        waitQueue = new LinkedList<Runnable>();
        lock = new ReentrantLock();
        for (int i = 0; i < size; i++) {
            workers[i] = new Thread(new Worker(waitQueue, lock));
            workers[i].start();
        }
    }


    /**
     * Create a thread pool with a specified size.
     *
     * @param int size The number of threads in the pool.
     */
    public ThreadPool(int size) {
        this.size = size;
        workers = new Thread[size];
        waitQueue = new LinkedList<Runnable>();
        lock = new ReentrantLock();
        for (int i = 0; i < size; i++)
            workers[i] = new Thread(new Worker(waitQueue, lock));
    }


    /**
     * shut down the pool.
     */
    public void shutdown() {
        for (int i = 0; i < workers.length; i++)
            workers[i].interrupt();
    }

    /**
     * Add work to the queue.
     */
    public void add(Runnable task) {
        lock.lock();
        waitQueue.offer(task);
        lock.unlock();
    }

}
