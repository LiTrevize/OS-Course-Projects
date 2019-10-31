import java.util.List;

public class FCFS implements Algorithm {

    private List<Task> queue;
    private int idx;

    public FCFS(List<Task> queue) {
        this.queue = queue;
        this.idx = 0;
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
