/**
 * "Virtual" CPU
 *
 * This virtual CPU also maintains system time.
 *
 * @author Greg Gagne - March 2016
 */
 
public class CPU
{
    /**
     * Run the specified task for the specified slice of time.
     * Return the CPU time this task runs
     */
    public static int run(Task task, int slice) {
        System.out.println("Will run " + task);
        // -1: not RR
        if (slice == -1) {
            int time = task.getBurst();
            System.out.format("Task %s finished.\n\n",task.getName());
            task.setBurst(0);
            return time;
        }
        // RR
        if (task.getBurst()<=slice) {
            int time = task.getBurst();
            task.setBurst(0);
            System.out.format("Task %s finished.\n\n",task.getName());
            return time;
        }
        else {
            task.setBurst(task.getBurst() - slice);
            return slice;
        }
    }
}
