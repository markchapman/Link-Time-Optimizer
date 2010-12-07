package optimizer.instrument;

import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public interface LibraryInfo {

    Set<Method> getBaseMethods();

    Set<JavaClass> getBaseTypes();

    Set<Method> getTransformations();

}
