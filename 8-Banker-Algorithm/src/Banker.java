import javax.swing.*;
import java.io.*;
import java.util.Arrays;

public class Banker {
    private int num_customer, num_resource;
    private int[] available;
    private int[][] maximum, allocation, need;

    public Banker(int a, int b, int[] avail, String filename) throws IOException {
        num_customer = a;
        num_resource = b;
        available = avail;
        maximum = new int[num_customer][num_resource];
        allocation = new int[num_customer][num_resource];
        need = new int[num_customer][num_resource];
        init(filename);
    }

    private void init(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        for (int i = 0; i < num_customer; i++) {
            String line = reader.readLine();
            String[] lines = line.split(",");
            for (int j = 0; j < lines.length; j++) {
                maximum[i][j] = Integer.parseInt(lines[j]);
                need[i][j] = maximum[i][j];
            }
        }
    }

    public int request_resources(int customer_id, int[] request) {
        // check if request exceed need
        for (int i = 0; i < num_resource; i++)
            if (need[customer_id][i] < request[i]) {
                System.out.println("Request denied.");
                return -1;
            }
        boolean[] finished = new boolean[num_customer];
        int[] free = new int[num_resource];
        // assume accept the request
        for (int i = 0; i < num_resource; i++) {
            available[i] -= request[i];
            allocation[customer_id][i] += request[i];
            need[customer_id][i] -= request[i];
            free[i] = available[i];
        }
        // banker's algorithm
        while (true) {
            int i;
            for (i = 0; i < num_customer; i++) {
                if (!finished[i]) {
                    int j = 0;
                    for (; j < num_resource; j++) {
                        if (need[i][j] > free[j]) break;
                    }
                    // all need are below available
                    if (j == num_resource) {
                        finished[i] = true;
                        for (int k = 0; k < num_resource; k++)
                            free[k] += allocation[i][k];
                        break;
                    }
                }
            }
            // no customer is satisfied in one round
            if (i == num_customer) break;
        }
        int i;
        for (i = 0; i < num_customer; i++)
            if (finished[i] == false) break;
        if (i == num_customer) {
            System.out.println("Request satisfied.");
            return 0;
        } else {
            // restore to original
            for (int j = 0; j < num_resource; j++) {
                available[j] += request[j];
                allocation[customer_id][j] -= request[j];
                need[customer_id][j] += request[j];
            }
            System.out.println("Request denied.");
            return -1;
        }
    }

    public void release_resources(int customer_id, int[] release) {
        // check if release exceed allocation
        // if so, set allocation to zero
        for (int i = 0; i < num_resource; i++) {
            if (release[i] >= allocation[customer_id][i]) {
                available[i] += allocation[customer_id][i];
                allocation[customer_id][i] = 0;
//                need[customer_id][i] = 0;
            } else {
                available[i] += release[i];
                allocation[customer_id][i] -= release[i];
            }

        }
    }

    public void print_info() {
        System.out.println("Available:");
        for (int i = 0; i < num_resource; i++)
            System.out.print(available[i] + " ");
        System.out.println();
        System.out.println("Maximum:");
        for (int i = 0; i < num_customer; i++) {
            for (int j = 0; j < num_resource; j++) {
                System.out.print(maximum[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Allocation:");
        for (int i = 0; i < num_customer; i++) {
            for (int j = 0; j < num_resource; j++) {
                System.out.print(allocation[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Need:");
        for (int i = 0; i < num_customer; i++) {
            for (int j = 0; j < num_resource; j++) {
                System.out.print(need[i][j] + " ");
            }
            System.out.println();
        }
    }
}
