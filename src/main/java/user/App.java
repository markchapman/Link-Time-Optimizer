package user;

import matrix.Matrix;
import matrix.MatrixFactory;
import matrix.MatrixOperations;

public class App {

    public static void main(String[] args) {
        Matrix mA = MatrixFactory.create(new double[][] {{1.0, 2.0}, {3.0, 4.0}});
        proc01(mA, 4);
    }

    public static double[] proc01(Matrix mA, int k) {
        mA = MatrixOperations.power(mA, k);
        double[] vL = new double[mA.getNumRows()];
        MatrixOperations.eigenvalues(mA, vL);
        return vL;
    }

}
