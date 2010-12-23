package user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class MatrixGenerator {

    public static void main(String[] args) throws FileNotFoundException
    {
        int n = Integer.parseInt(args[0]);
        PrintWriter out = new PrintWriter(new File(String.format("size%d.txt", n)));
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                out.printf(j == 0 ? "%5.2f" : "%6.2f", r.nextDouble()*3-1.5);
            out.println();
        }
        out.close();
    }

}
