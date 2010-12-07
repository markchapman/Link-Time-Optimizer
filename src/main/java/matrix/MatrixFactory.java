package matrix;

public class MatrixFactory
{
    public static Matrix create(double[][] data)
    {
        return new DefaultMatrix(data);
    }
}
