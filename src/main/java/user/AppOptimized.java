package user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import matrix.EigenDecompMatrix;
import matrix.Matrix;
import matrix.MatrixFactory;
import matrix.MatrixOperations;

public class AppOptimized {

    public static void main(String[] args) throws IOException
    {
        Matrix mA = MatrixFactory.read(new BufferedReader(new FileReader(args[0])));
        double[] l = proc01(mA, Integer.parseInt(args[1]));
        for (int i = 0; i < l.length; i++)
            System.out.println(l[i]);
    }

    public static double[] proc01(Matrix mA, int k)
    {
        mA = new EigenDecompMatrix(mA.getAsArray());
        mA = MatrixOperations.power(mA, k);
        return MatrixOperations.eigenvalues(mA);
    }

}
