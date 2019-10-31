import java.util.List;

public abstract class Scheduler implements Algorithm {
    // evaluation metrics
    protected int totalNum, incomplete, unResponded;
    protected double avgTurnaround, avgWaiting, avgResponse;
    protected byte[] responded;

    public Scheduler(List<Task> queue) {
        totalNum = queue.size();
        incomplete = queue.size();
        unResponded = queue.size();
        responded = new byte[queue.size()];
    }

    public Scheduler(List<Task> queue, int num) {
        totalNum = queue.size();
        incomplete = queue.size();
        unResponded = queue.size();
        responded = new byte[num];
    }

    public void calAvgTime(Task task, int time) {
        avgTurnaround += incomplete * time;
        avgWaiting += (incomplete - 1) * time;
        if (task.getBurst() == 0) {
            incomplete--;
        }
        if (responded[task.getTid()] == 0) {
            responded[task.getTid()] = 1;
            unResponded--;
        }
        avgResponse += unResponded * time;
        if (incomplete == 0) {
            avgWaiting /= totalNum;
            avgTurnaround /= totalNum;
            avgResponse /= totalNum;
        }
    }

    public void showAvgTime() {
        System.out.println("Average Turnaround Time: " + avgTurnaround);
        System.out.println("Average Waiting Time: " + avgWaiting);
        System.out.println("Average Response Time: " + avgResponse);
    }

    public double getAvgTurnaround() {
        return avgTurnaround;
    }

    public double getAvgWaiting() {
        return avgWaiting;
    }

    public double getAvgResponse() {
        return avgResponse;
    }
}
