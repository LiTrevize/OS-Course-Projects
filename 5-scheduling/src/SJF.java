import java.util.Comparator;
import java.util.List;

public class SJF extends Scheduler {

    private List<Task> queue;
    private int idx;

    public SJF(List<Task> queue) {
        super(queue);
        this.queue = queue;
        this.idx = 0;
        this.queue.sort(Comparator.comparingInt(Task::getBurst));
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
