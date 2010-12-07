package matrix;

import optimizer.instrument.Equivalents;

@Equivalents({"matrix.DefaultMatrix", "matrix.EigenDecompMatrix"})
public interface Matrix
{

    Matrix copy();

    double[][] getAsArray();

    double getElement(int row, int col);

    int getNumColumns();

    int getNumRows();

}
