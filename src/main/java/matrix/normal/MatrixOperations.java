package matrix.normal;

public class MatrixOperations {

    public static Matrix eigenvalues(Matrix mA) {
        return null; // TODO
    }

    public static Matrix mult(Matrix mA, Matrix mB) {
        double[][] mAB = new double[mA.getNumRows()][mB.getNumColumns()];
        for (int r = 1; r <= mAB.length; r++)
            for (int c = 1; c <= mAB[0].length; c++)
                for (int n = 1; n <= mA.getNumColumns(); n++)
                    mAB[r-1][c-1] += mA.getElement(r, n) * mB.getElement(n, c);
        return new DefaultMatrix(mAB);
    }

    public static Matrix power(Matrix mA, int k) {
        Matrix mP = new DefaultMatrix(mA);
        for (int i = 1; i < k; i++) {
            mP = mult(mP, mA);
        }
        return mP;
    }

}
