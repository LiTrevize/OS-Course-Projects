import java.util.Comparator;
import java.util.List;

public class Priority extends Scheduler {

    private List<Task> queue;
    private int idx;

    public Priority(List<Task> queue) {
        super(queue);
        this.queue = queue;
        this.idx = 0;
        this.queue.sort(Comparator.comparingInt(Task::getPriority).reversed());
    }

    @Override
    public void schedule() {
        int time;
        for (int i = 0; i < queue.size(); i++) {
            Task next_task = pickNextTask();
            time = CPU.run(next_task, -1);
            calAvgTime(next_task, time);
        }
    }

    @Override
    public Task pickNextTask() {
        return queue.get(idx++);
    }


}
