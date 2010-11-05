package matrix.normal;

import java.util.Arrays;

public class DefaultMatrix implements Matrix {

    private final double[][] data;

    public DefaultMatrix(double[][] data) {
        this.data = getCopy(data);
    }

    public DefaultMatrix(Matrix mA) {
        this.data = mA.getAsArray();
    }

    @Override
    public double[][] getAsArray() {
        return getCopy(data);
    }

    @Override
    public double getElement(int row, int col) {
        return data[row-1][col-1];
    }

    @Override
    public int getNumColumns() {
        return data[0].length;
    }

    @Override
    public int getNumRows() {
        return data.length;
    }

    private static double[][] getCopy(double[][] data) {
        double[][] copy = new double[data.length][];
        for (int i = 0; i < data.length; i++)
            copy[i] = Arrays.copyOf(data[i], data[i].length);
        return copy;
    }

}
