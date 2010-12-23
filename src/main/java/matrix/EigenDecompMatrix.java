package matrix;

import static matrix.Utils.*;
import java.util.Arrays;
import Jama.EigenvalueDecomposition;
import lto.libinfo.Cost;

public class EigenDecompMatrix implements Matrix
{
    private final double[][] V; // eigenvectors
    private final double[] L; // eigenvalues
    private double[][] data; // original matrix

    public EigenDecompMatrix(double[][] data)
    {
        EigenvalueDecomposition E = (new Jama.Matrix(data)).eig();
        V = E.getV().getArray();
        L = E.getRealEigenvalues();
        this.data = getCopy(data);
    }

    public EigenDecompMatrix(EigenDecompMatrix A) {
        V = getCopy(A.V);
        L = Arrays.copyOf(A.L, A.L.length);
        data = getCopy(A.data);
    }

    @Override
    public EigenDecompMatrix copy()
    {
        return new EigenDecompMatrix(this);
    }

    @Override
    public double[][] getAsArray() {
        if (data == null) {
            Jama.Matrix mV = new Jama.Matrix(V), mL = new Jama.Matrix(L.length, L.length);
            for (int i = 0; i < L.length; i++)
                mL.set(i, i, L[i]);
            data = mV.times(mL.times(mV.inverse())).getArray();
        }
        return getCopy(data);
    }

    @Override
    public double getElement(int row, int col) {
        return getAsArray()[row - 1][col - 1];
    }

    @Override
    public int getNumColumns() {
        return L.length;
    }

    @Override
    public int getNumRows() {
        return L.length;
    }

    @Cost(10)
    public static double[] eigenvalues(EigenDecompMatrix A)
    {
        return Arrays.copyOf(A.L, A.L.length);
    }

    @Cost(20000)
    public static EigenDecompMatrix multiply(EigenDecompMatrix mA, EigenDecompMatrix mB)
    {
        return new EigenDecompMatrix(DefaultMatrix.multiply(new DefaultMatrix(mA), new DefaultMatrix(mB)).getAsArray());
    }

    @Cost(100)
    public static EigenDecompMatrix power(EigenDecompMatrix A, int k)
    {
        EigenDecompMatrix mP = A.copy();
        mP.data = null; // must recompute, if desired later
        for (int i = 0; i < mP.L.length; i++)
            mP.L[i] = Math.pow(mP.L[i], k);
        return mP;
    }

}
