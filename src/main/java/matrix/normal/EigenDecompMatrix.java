package matrix.normal;

import java.util.Arrays;

public class EigenDecompMatrix implements Matrix
{
    private final double[][] U;
    private final double[] L; //lambda

    // remove
    public EigenDecompMatrix()
    {
        U = new double[1][1];
        L = new double[1];
    }
    
    public EigenDecompMatrix(double[][] U, double[] L)
    {
        this.U = U;
        this.L = L;
    }

    @Override
    public double[][] getAsArray() {
        return getCopy(U);
    }

    @Override
    public double getElement(int row, int col) {
        return U[row-1][col-1];
    }

    @Override
    public int getNumColumns() {
        return U[0].length;
    }

    @Override
    public int getNumRows() {
        return U.length;
    }

    private static double[][] getCopy(double[][] data) {
        double[][] copy = new double[data.length][];
        for (int i = 0; i < data.length; i++)
            copy[i] = Arrays.copyOf(data[i], data[i].length);
        return copy;
    }

    @Cost(constant=40.0, lognexp=0.0, nexp=3.0)
    public static EigenDecompMatrix power(EigenDecompMatrix A, int k)
    {
        EigenDecompMatrix mP = new EigenDecompMatrix();
        return mP;
    }
    
    @Cost(constant=10.0, lognexp=0.0, nexp=3.0)
    public static EigenDecompMatrix svd(EigenDecompMatrix A)
    {
        return A;
    }
}
