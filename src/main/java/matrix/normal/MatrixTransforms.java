package matrix.normal;

public class MatrixTransforms
{
    public static EigenDecompMatrix DefaultMatrixToEigenDecompMatrix(DefaultMatrix A)
    {
        return DefaultMatrix.svd(A);
    }
    
    public static DefaultMatrix EigenDecompMatrixToDefaultMatrix(EigenDecompMatrix A)
    {
        int n = A.getNumRows();
        DefaultMatrix B = new DefaultMatrix(n);
        // B = U * L * U*
        return B;
    }
}
