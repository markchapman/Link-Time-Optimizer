package optimizer.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;

public class ListAnnotations implements ClassFileTransformer {

    static {
        Attribute.addAttributeReader("RuntimeVisibleAnnotations", new AnnotationReader());
        Attribute.addAttributeReader("RuntimeInvisibleAnnotations", new AnnotationReader());
    };

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ListAnnotations());
    }

    @Override
    public byte[] transform(ClassLoader cl, String name, Class<?> type, ProtectionDomain domain, byte[] code)
            throws IllegalClassFormatException {
        if (name.startsWith("java/"))
            return code;
        try {
            System.out.println("Listing annotations for: " + name);
            ClassParser cp = new ClassParser(new ByteArrayInputStream(code), name + ".java");
            JavaClass jc = cp.parse();
            ClassGen cg = new ClassGen(jc);
            for (Method m : cg.getMethods()) {
                Collection<Annotation> can = AnnotationReader.getAnnotations(m.getAttributes());
                if (!can.isEmpty()) {
                    System.out.println("  method: " + m.getName());
                    for (Annotation an : can)
                        System.out.println("    " + an);
                }
            }
            return code;
        } catch (IOException e) {
            throw new RuntimeException("Cannot find class: " + name, e);
        }
    }

}
