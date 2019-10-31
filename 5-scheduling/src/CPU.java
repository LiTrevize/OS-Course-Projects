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
     */
    public static void run(Task task, int slice) {
        System.out.println("Will run " + task);
        // -1: not RR
        if (slice == -1) {
            System.out.format("Task %s finished.\n\n",task.getName());
            return;
        }
        // RR
        if (task.getBurst()<=slice) {
            task.setBurst(0);
            System.out.format("Task %s finished.\n\n",task.getName());
        }
        else
            task.setBurst(task.getBurst()-slice);
    }
}
