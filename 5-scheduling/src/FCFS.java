import java.util.List;

public class FCFS extends Scheduler {

    private List<Task> queue;
    private int idx;

    public FCFS(List<Task> queue) {
        super(queue);
        this.queue = queue;
        this.idx = 0;
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
