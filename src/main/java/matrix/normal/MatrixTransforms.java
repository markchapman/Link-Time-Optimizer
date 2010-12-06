package matrix.normal;

public class MatrixTransforms
{

    public static EigenDecompMatrix DefaultMatrixToEigenDecompMatrix(DefaultMatrix A)
    {
        return DefaultMatrix.svd(A);
    }

    public static DefaultMatrix EigenDecompMatrixToDefaultMatrix(EigenDecompMatrix A)
    {
        return new DefaultMatrix(A.getAsArray());
    }

}
