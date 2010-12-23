package matrix;

import static lto.Utils.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class Manifest implements lto.libinfo.Manifest {

    private static final Set<JavaClass> BT = new HashSet<JavaClass>();
    private static final Set<Method> BLM = new HashSet<Method>(), LS = new HashSet<Method>();

    static {
        // base types
        BT.add(rlk("matrix.Matrix"));
        // base methods
        BLM.add(rlm("matrix.MatrixOperations", "eigenvalues"));
        BLM.add(rlm("matrix.MatrixOperations", "multiply"));
        BLM.add(rlm("matrix.MatrixOperations", "power"));
        // transformations
        LS.add(rlm("matrix.MatrixTransforms", "DefaultMatrixToEigenDecompMatrix"));
        LS.add(rlm("matrix.MatrixTransforms", "EigenDecompMatrixToDefaultMatrix"));
    }

    @Override
    public Set<Method> getBaseMethods() {
        return BLM;
    }

    @Override
    public Set<JavaClass> getBaseTypes() {
        return BT;
    }

    @Override
    public Set<Method> getTransformations() {
        return LS;
    }

}
