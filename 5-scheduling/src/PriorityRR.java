import java.util.*;

public class PriorityRR implements Algorithm {

    private List<Task> queue;
    private int idx;
    private int slice;

    public PriorityRR(List<Task> queue) {
        this.idx = 0;
        this.slice = 10;
        this.queue = queue;
        this.queue.sort(Comparator.comparingInt(Task::getPriority).reversed());
    }

    @Override
    public void schedule() {
        int i = 0, j = 0;
        for (j = 0; j < queue.size(); j++) {
            // encounter different task priority groups
            if (j < queue.size() - 1 && queue.get(j).getPriority() != queue.get(j + 1).getPriority() || j == queue.size() - 1) {
                // if only one task in the group
                if (i == j) {
                    CPU.run(queue.get(j), -1);
                } else {
                    Algorithm scheduler = new RR(queue.subList(i, j+1));
                    scheduler.schedule();
                }
                i = j + 1;
            }
        }
    }

    @Override
    public Task pickNextTask() {
        return null;
    }
}