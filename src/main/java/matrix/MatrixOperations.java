package matrix;

import lto.libinfo.Equivalents;

public class MatrixOperations {

    @Equivalents("matrix.DefaultMatrix.eigenvalues,matrix.EigenDecompMatrix.eigenvalues")
    public static double[] eigenvalues(Matrix A)
    {
        if (A instanceof DefaultMatrix)
            return DefaultMatrix.eigenvalues((DefaultMatrix) A);
        else if (A instanceof EigenDecompMatrix)
            return EigenDecompMatrix.eigenvalues((EigenDecompMatrix) A);
        return DefaultMatrix.eigenvalues(new DefaultMatrix(A));
    }

    @Equivalents("matrix.DefaultMatrix.multiply,matrix.EigenDecompMatrix.multiply")
    public static Matrix multiply(Matrix A, Matrix B)
    {
        if (A instanceof DefaultMatrix && B instanceof DefaultMatrix)
            return DefaultMatrix.multiply((DefaultMatrix) A, (DefaultMatrix) B);
        else if (A instanceof EigenDecompMatrix && B instanceof EigenDecompMatrix)
            return EigenDecompMatrix.multiply((EigenDecompMatrix) A, (EigenDecompMatrix) B);
        return DefaultMatrix.multiply(new DefaultMatrix(A), new DefaultMatrix(B));
    }

    @Equivalents("matrix.DefaultMatrix.power,matrix.EigenDecompMatrix.power")
    public static Matrix power(Matrix A, int k)
    {
        if (A instanceof DefaultMatrix)
            return DefaultMatrix.power((DefaultMatrix) A, k);
        else if (A instanceof EigenDecompMatrix)
            return EigenDecompMatrix.power((EigenDecompMatrix) A, k);
        return DefaultMatrix.power(new DefaultMatrix(A), k);
    }

}
