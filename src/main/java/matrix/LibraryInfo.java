package matrix;

import static optimizer.instrument.Utils.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class LibraryInfo implements optimizer.instrument.LibraryInfo {

    private static final Set<JavaClass> bt = new HashSet<JavaClass>();
    private static final Set<Method> bm = new HashSet<Method>(), tt = new HashSet<Method>();

    static {
        // base types
        bt.add(rlk("matrix.normal.Matrix"));
        // base methods
        bm.add(rlm("matrix.normal.MatrixOperations", "eigenvalues"));
        bm.add(rlm("matrix.normal.MatrixOperations", "multiply"));
        bm.add(rlm("matrix.normal.MatrixOperations", "power"));
        bm.add(rlm("matrix.normal.MatrixOperations", "svd"));
        // transformations
        tt.add(rlm("matrix.normal.MatrixTransforms", "DefaultMatrixToEigenDecompMatrix"));
        tt.add(rlm("matrix.normal.MatrixTransforms", "EigenDecompMatrixToDefaultMatrix"));
    }

    @Override
    public Set<Method> getBaseMethods() {
        return bm;
    }

    @Override
    public Set<JavaClass> getBaseTypes() {
        return bt;
    }

    @Override
    public Set<Method> getTransformations() {
        return tt;
    }

}
