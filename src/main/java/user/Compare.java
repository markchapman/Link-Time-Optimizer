package user;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Compare {

    public static void main(String[] args) throws IOException
    {
        long t1 = System.nanoTime();
        App.main(args);
        System.out.printf("%n---%n%n");
        long t2 = System.nanoTime();
        AppOptimized.main(args);
        System.out.printf("%n---%n%n");
        long t3 = System.nanoTime();
        System.out.printf("Unoptimized: %10.3f ms%nOptimized:   %10.3f ms%n---%n", (t2-t1)*.000001, (t3-t2)*.000001);

        PrintWriter f = new PrintWriter(new FileWriter("comparison.txt", true));
        f.printf("%s %s%n", args[0], args[1]);
        f.printf("Unoptimized: %10.3f ms%nOptimized:   %10.3f ms%n---%n", (t2-t1)*.000001, (t3-t2)*.000001);
        f.close();
    }

}
