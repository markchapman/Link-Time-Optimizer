package matrix;

import java.util.Arrays;

public class Utils {

    public static double[][] getCopy(double[][] data)
    {
        double[][] copy = new double[data.length][];
        for (int i = 0; i < data.length; i++)
            copy[i] = Arrays.copyOf(data[i], data[i].length);
        return copy;
    }

}
