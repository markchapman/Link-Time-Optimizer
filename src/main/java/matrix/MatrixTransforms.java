package matrix;

import lto.libinfo.Cost;

public class MatrixTransforms
{

    @Cost(10000) // TODO cost of DefaultMatrixToEigenDecompMatrix (10000?)
    public static EigenDecompMatrix DefaultMatrixToEigenDecompMatrix(DefaultMatrix A)
    {
        return new EigenDecompMatrix(A);
    }

    @Cost(100) // TODO cost of EigenDecompMatrixToDefaultMatrix (100?)
    public static DefaultMatrix EigenDecompMatrixToDefaultMatrix(EigenDecompMatrix A)
    {
        return new DefaultMatrix(A);
    }

}
