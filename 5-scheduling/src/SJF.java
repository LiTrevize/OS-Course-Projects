import java.util.Comparator;
import java.util.List;

public class SJF implements Algorithm {

    private List<Task> queue;
    private int idx;

    public SJF(List<Task> queue) {
        this.queue = queue;
        this.idx = 0;
        this.queue.sort(Comparator.comparingInt(Task::getBurst));
    }

    @Override
    public void schedule() {
        for (int i = 0; i < queue.size(); i++) {
            Task next_task = pickNextTask();
            CPU.run(next_task, -1);
        }
    }

    @Override
    public Task pickNextTask() {
        return queue.get(idx++);
    }

}
