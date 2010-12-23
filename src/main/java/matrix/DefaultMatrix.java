package matrix;

import static matrix.Utils.*;
import lto.libinfo.Cost;

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
    public DefaultMatrix copy()
    {
        return new DefaultMatrix(data);
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

    @Cost(10000)
    public static double[] eigenvalues(DefaultMatrix A) {
        return (new Jama.Matrix(A.data)).eig().getRealEigenvalues();
    }

    @Cost(1000)
    public static DefaultMatrix multiply(DefaultMatrix mA, DefaultMatrix mB) {
        double[][] mAB = new double[mA.getNumRows()][mB.getNumColumns()];
        for (int r = 1; r <= mAB.length; r++)
            for (int c = 1; c <= mAB[0].length; c++)
                for (int n = 1; n <= mA.getNumColumns(); n++)
                    mAB[r-1][c-1] += mA.getElement(r, n) * mB.getElement(n, c);
        return new DefaultMatrix(mAB);
    }

    @Cost(10000)
    public static DefaultMatrix power(DefaultMatrix mA, int k)
    {
        DefaultMatrix mP = mA.copy();
        for (int i = 1; i < k; i++)
            mP = multiply(mP, mA);
        return mP;
    }

}
