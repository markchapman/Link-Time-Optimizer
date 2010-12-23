package matrix;

import lto.libinfo.Cost;

public class MatrixTransforms
{

    @Cost(10000)
    public static EigenDecompMatrix DefaultMatrixToEigenDecompMatrix(DefaultMatrix A)
    {
        return new EigenDecompMatrix(A.getAsArray());
    }

    @Cost(10000)
    public static DefaultMatrix EigenDecompMatrixToDefaultMatrix(EigenDecompMatrix A)
    {
        return new DefaultMatrix(A);
    }

}
