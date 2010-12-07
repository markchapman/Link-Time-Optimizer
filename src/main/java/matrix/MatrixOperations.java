package matrix;

import optimizer.instrument.Equivalents;

public class MatrixOperations {

    @Equivalents({"matrix.DefaultMatrix.eigenvalues", "matrix.EigenDecompMatrix.eigenvalues"})
    public static void eigenvalues(Matrix A, double[] L)
    {
        if (A instanceof DefaultMatrix)
            DefaultMatrix.eigenvalues((DefaultMatrix) A, L);
        else if (A instanceof EigenDecompMatrix)
            EigenDecompMatrix.eigenvalues((EigenDecompMatrix) A, L);
    }

    @Equivalents({"matrix.DefaultMatrix.multiply", "matrix.EigenDecompMatrix.multiply"})
    public static Matrix multiply(Matrix A, Matrix B)
    {
        if (A instanceof DefaultMatrix && B instanceof DefaultMatrix)
            return DefaultMatrix.multiply((DefaultMatrix) A, (DefaultMatrix) B);
        else if (A instanceof EigenDecompMatrix && B instanceof EigenDecompMatrix)
            return EigenDecompMatrix.multiply((EigenDecompMatrix) A, (EigenDecompMatrix) B);
        return DefaultMatrix.multiply(new DefaultMatrix(A), new DefaultMatrix(B));
    }

    @Equivalents({"matrix.DefaultMatrix.power", "matrix.EigenDecompMatrix.power"})
    public static Matrix power(Matrix A, int k)
    {
        if (A instanceof DefaultMatrix)
            return DefaultMatrix.power((DefaultMatrix) A, k);
        else if (A instanceof EigenDecompMatrix)
            return EigenDecompMatrix.power((EigenDecompMatrix) A, k);
        return DefaultMatrix.power(new DefaultMatrix(A), k);
    }

    @Equivalents({"matrix.DefaultMatrix.svd", "matrix.EigenDecompMatrix.svd"})
    public static void svd(Matrix A, double[][] U, double[] L)
    {
        int n = A.getNumRows();
        assert n == A.getNumColumns();
        assert n == U.length;
        assert n == U[0].length;
        assert n == L.length;

        if (A instanceof DefaultMatrix)
            DefaultMatrix.svd((DefaultMatrix) A, U, L);
        else if (A instanceof EigenDecompMatrix)
            EigenDecompMatrix.svd((EigenDecompMatrix) A, U, L);
    }

}
