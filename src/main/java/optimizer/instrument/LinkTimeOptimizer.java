package optimizer.instrument;

import static optimizer.instrument.Utils.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;

public class LinkTimeOptimizer implements ClassFileTransformer {

    static {
        Attribute.addAttributeReader("RuntimeVisibleAnnotations", new AnnotationReader());
        Attribute.addAttributeReader("RuntimeInvisibleAnnotations", new AnnotationReader());
    };

    public static void premain(String args, Instrumentation inst) throws Exception
    {
        inst.addTransformer(new LinkTimeOptimizer(args));
    }

    private final LibraryInfo                         li;  // library info
    private final Map<Method, Set<Method>>            em = // method equivalents
            new HashMap<Method, Set<Method>>();
    private final Map<JavaClass, Set<JavaClass>>      et = // type equivalents
            new HashMap<JavaClass, Set<JavaClass>>();
    private final Map<Method, Integer>                cm = // method costs
            new HashMap<Method, Integer>();
    private final Map<Method, Integer>                ct = // transformation costs
            new HashMap<Method, Integer>();

    public LinkTimeOptimizer(String info) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        li = (LibraryInfo) Class.forName(info).newInstance();
        readEquivalentMethods();
        readEquivalentTypes();
        readMethodCosts();
        readTransformationCosts();
        System.out.println(this);
    }

    private void readEquivalentMethods()
    {
        for (Method m : li.getBaseMethods()) {
            for (Annotation an : AnnotationReader.getAnnotations(m.getAttributes())) {
                if (an instanceof Equivalents) {
                    Set<Method> e = new HashSet<Method>();
                    for (String s : ((Equivalents) an).value().split(",")) {
                        int i = s.lastIndexOf('.');
                        e.add(rlm(s.substring(0, i), s.substring(i + 1)));
                    }
                    em.put(m, e);
                }
            }
        }
    }

    private void readEquivalentTypes()
    {
        for (JavaClass t : li.getBaseTypes()) {
            for (Annotation an : AnnotationReader.getAnnotations(t.getAttributes())) {
                if (an instanceof Equivalents) {
                    Set<JavaClass> e = new HashSet<JavaClass>();
                    for (String s : ((Equivalents) an).value().split(","))
                        e.add(rlk(s));
                    et.put(t, e);
                }
            }
        }
    }

    private void readMethodCosts()
    {
        Set<Method> all = new HashSet<Method>();
        for (Set<Method> sm : em.values())
            for (Method m : sm)
                all.add(m);
        for (Method m : all)
            for (Annotation an : AnnotationReader.getAnnotations(m.getAttributes()))
                if (an instanceof Cost)
                    cm.put(m, ((Cost)an).value());
    }

    private void readTransformationCosts()
    {
        for (Method m : li.getTransformations()) // TODO check that types are equivalent?
            for (Annotation an : AnnotationReader.getAnnotations(m.getAttributes()))
                if (an instanceof Cost)
                    ct.put(m, ((Cost)an).value());
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        String n = String.format("%n");

        s.append("Equivalent methods:" + n);
        for (Method m : li.getBaseMethods()) {
            s.append("  " + m.getName() + " -> { ");
            for (Method e : em.get(m))
                s.append(e.getReturnType() + ":" + e.getName() + " "); // TODO fix this info
            s.append("}" + n);
        }

        s.append("Equivalent types:" + n);
        for (JavaClass t : li.getBaseTypes()) {
            s.append("  " + t.getClassName() + " -> { ");
            for (JavaClass e : et.get(t))
                s.append(e.getClassName() + " ");
            s.append("}" + n);
        }

        s.append("Method costs:" + n);
        for (Method m : cm.keySet())
            s.append("  " + m.getName() + " -> " + cm.get(m) + n);

        s.append("Transformation costs:" + n);
        for (Method m : ct.keySet())
            s.append("  " + m.getName() + " -> " + ct.get(m) + n);

        return s.toString();
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
