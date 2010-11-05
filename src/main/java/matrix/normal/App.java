package matrix.normal;

public class App {

    public static void main(String[] args) {
        Matrix mA = MatrixFactory.create(new double[][] {{1.0, 2.0}, {3.0, 4.0}});
        op(mA, 4);
    }

    public static void op(Matrix mA, int k) {
        MatrixOperations.power(mA, k);
        MatrixOperations.eigenvalues(mA);
    }

}
