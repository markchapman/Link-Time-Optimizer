package lto.libinfo;

import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public interface Manifest {

    Set<Method> getBaseMethods();

    Set<JavaClass> getBaseTypes();

    Set<Method> getTransformations();

}
