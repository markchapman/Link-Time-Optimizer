package matrix.normal;

public interface Matrix {

    double[][] getAsArray();

    double getElement(int row, int col);

    int getNumColumns();

    int getNumRows();

}
