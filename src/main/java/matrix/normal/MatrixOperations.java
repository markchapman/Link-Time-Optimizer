package matrix.normal;

public class MatrixOperations {

    public static Matrix eigenvalues(Matrix mA) {
        return null; // TODO
    }

    public static Matrix power(Matrix mA, int k) {
        Matrix mP = mA.copy();
        for (int i = 1; i < k; i++)
            mP = MatrixUtils.mult(mP, mA);
        return mP;
    }

}
