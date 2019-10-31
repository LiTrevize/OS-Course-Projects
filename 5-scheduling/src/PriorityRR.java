import java.util.*;

public class PriorityRR extends Scheduler {

    private List<Task> queue;
    private int idx;
    private final int slice = 10;

    public PriorityRR(List<Task> queue) {
        super(queue);
        this.idx = 0;
        this.queue = queue;
        this.queue.sort(Comparator.comparingInt(Task::getPriority).reversed());
    }

    @Override
    public void schedule() {
        int i = 0, j = 0, time;
        for (j = 0; j < queue.size(); j++) {
            // encounter different task priority groups
            if (j < queue.size() - 1 && queue.get(j).getPriority() != queue.get(j + 1).getPriority() || j == queue.size() - 1) {
                // if only one task in the group
                if (i == j) {
                    time = CPU.run(queue.get(j), -1);
                    calAvgTime(queue.get(j), time);
                } else {
                    RR scheduler = new RR(queue.subList(i, j + 1), totalNum);
                    scheduler.schedule();
                    calRRPhase(scheduler, j + 1 - i);
                }
                i = j + 1;
            }
        }
    }

    @Override
    public Task pickNextTask() {
        return null;
    }

    public void calRRPhase(RR scheduler, int numTask) {
        // for RR tasks
        avgTurnaround += scheduler.getAvgTurnaround() * numTask;
        avgWaiting += scheduler.getAvgWaiting() * numTask;
        avgResponse += scheduler.getAvgResponse() * numTask;
        // for other tasks
        incomplete -= numTask;
        unResponded -= numTask;
        int timeElapsed = scheduler.getTimeElapsed();
        avgTurnaround += incomplete * timeElapsed;
        avgWaiting += incomplete * timeElapsed;
        avgResponse += incomplete * timeElapsed;
    }
}