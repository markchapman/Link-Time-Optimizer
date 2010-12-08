package matrix;

import static matrix.Utils.*;

import java.util.Arrays;

import lto.libinfo.Cost;


public class EigenDecompMatrix implements Matrix
{
    private final double[][] U;
    private final double[] L; //lambda

    public EigenDecompMatrix(double[][] U, double[] L)
    {
        this.U = getCopy(U);
        this.L = Arrays.copyOf(L, L.length);
    }

    public EigenDecompMatrix(DefaultMatrix A)
    {
        int n = A.getNumRows();
        U = new double[n][n];
        L = new double[n];
        DefaultMatrix.svd(A, U, L);
    }

    @Override
    public EigenDecompMatrix copy()
    {
        return new EigenDecompMatrix(U, L);
    }

    @Override
    public double[][] getAsArray()
    {
        return getCopy(U); // TODO getAsArray()
    }

    @Override
    public double getElement(int row, int col)
    {
        return U[row-1][col-1]; // TODO getElement()
    }

    @Override
    public int getNumColumns()
    {
        return U[0].length;
    }

    @Override
    public int getNumRows()
    {
        return U.length;
    }

    @Cost(1) // TODO cost of eigenvalues (1?)
    public static void eigenvalues(EigenDecompMatrix A, double[] L)
    {
        for (int i = 0; i < L.length; i++)
            L[i] = A.L[i];
    }

    @Cost(40) // TODO cost of multiply (40?)
    public static EigenDecompMatrix multiply(EigenDecompMatrix mA, EigenDecompMatrix mB)
    {
        return null; // TODO multiply
    }

    @Cost(400) // TODO cost of power (400?)
    public static EigenDecompMatrix power(EigenDecompMatrix A, int k)
    {
        EigenDecompMatrix mP = A.copy();
        for (int i = 0; i < mP.L.length; i++)
            mP.L[i] = Math.pow(mP.L[i], k);
        return mP;
    }

    @Cost(10) // TODO cost of svd (10?)
    public static void svd(EigenDecompMatrix A, double[][] U, double[] L)
    {
        int n = L.length;
        for (int i = 0; i < n; i++) {
            L[i] = A.L[i];
            for (int j = 0; j < n; j++)
                U[i][j] = A.U[i][j];
        }
    }

}
