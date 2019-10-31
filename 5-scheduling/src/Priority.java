import java.util.Comparator;
import java.util.List;

public class Priority implements Algorithm {

    private List<Task> queue;
    private int idx;

    public Priority(List<Task> queue) {
        this.queue = queue;
        this.idx = 0;
        this.queue.sort(Comparator.comparingInt(Task::getPriority).reversed());
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
