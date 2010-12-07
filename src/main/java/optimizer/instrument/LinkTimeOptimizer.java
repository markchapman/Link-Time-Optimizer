package optimizer.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;

public class LinkTimeOptimizer implements ClassFileTransformer {

    static {
        Attribute.addAttributeReader("RuntimeVisibleAnnotations", new AnnotationReader());
        Attribute.addAttributeReader("RuntimeInvisibleAnnotations", new AnnotationReader());
    };

    public static void premain(String args, Instrumentation inst) throws Exception
    {
        inst.addTransformer(new LinkTimeOptimizer(new LibInfo(args)));
    }

    private LibInfo li;

    public LinkTimeOptimizer(LibInfo li) {
        this.li = li;
        System.out.println(this.li);
    }

    @Override
    public byte[] transform(ClassLoader cl, String name, Class<?> type, ProtectionDomain domain, byte[] code)
            throws IllegalClassFormatException {
        if (name.startsWith("java"))
            return code;
        try {
            ClassParser cp = new ClassParser(new ByteArrayInputStream(code), name + ".java");
            JavaClass jc = cp.parse();
            ClassGen cg = new ClassGen(jc);
            System.out.println("This is when " + cg.getClassName() + " would be optimized.");
        } catch (IOException e) {
            throw new RuntimeException("Cannot find class: " + name, e);
        }
        return code;
    }

}
