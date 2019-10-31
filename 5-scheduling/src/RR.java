import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RR extends Scheduler {

    private Queue<Task> queue;
    private int slice;
    private int timeElapsed;

    public RR(List<Task> queue) {
        super(queue);
        this.slice = 10;
        this.queue = new LinkedList<Task>();
        for (int i = 0; i < queue.size(); i++) {
            this.queue.offer(queue.get(i));
        }
        timeElapsed = 0;
    }

    public RR(List<Task> queue, int num) {
        super(queue, num);
        this.slice = 10;
        this.queue = new LinkedList<Task>();
        for (int i = 0; i < queue.size(); i++) {
            this.queue.offer(queue.get(i));
        }
        timeElapsed = 0;
    }

    @Override
    public void schedule() {
        int time;
        while (!queue.isEmpty()) {
            Task next_task = pickNextTask();
            time = CPU.run(next_task, slice);
            timeElapsed += time;
            calAvgTime(next_task, time);
            if (next_task.getBurst() > 0)
                queue.offer(next_task);
        }
    }

    @Override
    public Task pickNextTask() {
        return queue.poll();
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }
}