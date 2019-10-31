import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RR implements Algorithm {

    private Queue<Task> queue;
    private int idx;
    private int slice;

    public RR(List<Task> queue) {
        this.idx = 0;
        this.slice = 10;
        this.queue = new LinkedList<Task>();
        for (int i = 0; i < queue.size(); i++) {
            this.queue.offer(queue.get(i));
        }
    }

    @Override
    public void schedule() {
        while (!queue.isEmpty()) {
            Task next_task = pickNextTask();
            CPU.run(next_task, slice);
            if (next_task.getBurst()>0)
                queue.offer(next_task);
        }
    }

    @Override
    public Task pickNextTask() {
        return queue.poll();
    }
}