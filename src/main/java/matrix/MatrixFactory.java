package matrix;

import java.io.BufferedReader;
import java.io.IOException;

public class MatrixFactory
{

    public static Matrix create(double[][] data)
    {
        return new DefaultMatrix(data);
    }

    public static Matrix read(BufferedReader stream) throws IOException
    {
        return new DefaultMatrix(Jama.Matrix.read(stream).getArray());
    }

}
