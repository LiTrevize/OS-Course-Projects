import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Driver {

    public static void main(String[] args) throws IOException {
        int[] available = new int[args.length];
        for (int i = 0; i < args.length; i++)
            available[i] = Integer.parseInt(args[i]);
        Banker banker = new Banker(5, 4, available, "input.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (true) {
            line = reader.readLine();
            if (line.equals("exit")) break;
            if (line.equals("*")) {
                banker.print_info();
            } else if (line.charAt(0) == 'R' && line.charAt(1) == 'Q') {
                String[] lines = line.substring(2).strip().split(" ");
                int[] request = new int[lines.length - 1];
                for (int i = 0; i < request.length; i++) {
                    request[i] = Integer.parseInt(lines[i + 1]);
                }
                banker.request_resources(Integer.parseInt(lines[0]), request);
            } else if (line.charAt(0) == 'R' && line.charAt(1) == 'L') {
                String[] lines = line.substring(2).strip().split(" ");
                int[] release = new int[lines.length - 1];
                for (int i = 0; i < release.length; i++) {
                    release[i] = Integer.parseInt(lines[i + 1]);
                }
                banker.release_resources(Integer.parseInt(lines[0]), release);
            }
        }
    }
}
