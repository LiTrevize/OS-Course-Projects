import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Driver {

    public static void main(String[] args) throws IOException {
        if (args.length==0) return;
        Allocator alloc=new Allocator(Integer.parseInt(args[0]));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        String[] lines;
        while (true) {
            System.out.print("allocator>");
            line = reader.readLine();
            lines=line.split(" ");
            if (lines[0].equals("exit")) break;
            if (lines[0].equals("STAT")) {
                alloc.status();
            } else if (lines[0].equals("C")) {
                alloc.compact();
            } else if (lines[0].equals("RQ")) {
                alloc.request(Integer.parseInt(lines[1].substring(1)),Integer.parseInt(lines[2]),lines[3].charAt(0));
            } else if (lines[0].equals("RL")) {
                alloc.release(Integer.parseInt(lines[1].substring(1)));
            }
        }
    }
}
