package matrix.normal;

import java.util.Arrays;

public class DefaultMatrix implements Matrix
{
    private final double[][] data;

    public DefaultMatrix(int n)
    {
        this.data = new double[n][n];
    }
    
    public DefaultMatrix(double[][] data)
    {
        this.data = getCopy(data);
    }

    public DefaultMatrix(Matrix mA)
    {
        this.data = mA.getAsArray();
    }

    @Override
    public double[][] getAsArray()
    {
        return getCopy(data);
    }

    @Override
    public double getElement(int row, int col)
    {
        return data[row - 1][col - 1];
    }

    @Override
    public int getNumColumns()
    {
        return data[0].length;
    }

    @Override
    public int getNumRows()
    {
        return data.length;
    }

    private static double[][] getCopy(double[][] data)
    {
        double[][] copy = new double[data.length][];
        for (int i = 0; i < data.length; i++)
            copy[i] = Arrays.copyOf(data[i], data[i].length);
        return copy;
    }

    @Cost(constant=40.0, lognexp=0.0, nexp=3.0)
    public static DefaultMatrix power(DefaultMatrix mA, int k)
    {
        DefaultMatrix mP = new DefaultMatrix(mA);
        for (int i = 1; i < k; i++)
            mP = (DefaultMatrix)MatrixUtils.mult(mP, mA);
        return mP;
    }

    @Cost(constant=10.0, lognexp=0.0, nexp=3.0)
    public static EigenDecompMatrix svd(DefaultMatrix A)
    {
        int n = A.getNumRows();
        double[][] D = A.getAsArray();
        //assert rows == cols
        
        double[][] U = new double[2][2];
        double[] L = new double[2];
        MatrixUtils.svd(n, D, U, L);
        return new EigenDecompMatrix(U, L);
    }
}
