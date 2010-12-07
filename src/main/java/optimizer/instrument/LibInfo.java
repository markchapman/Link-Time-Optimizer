package optimizer.instrument;

import static optimizer.instrument.Utils.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class LibInfo {

    private final Manifest manifest;
    final Map<Method, Set<Method>>            em = // method equivalents
            new HashMap<Method, Set<Method>>();
    final Map<JavaClass, Set<JavaClass>>      et = // type equivalents
            new HashMap<JavaClass, Set<JavaClass>>();
    final Map<Method, Integer>                cem = // method costs
            new HashMap<Method, Integer>();
    final Map<Method, Integer>                ctt = // transformation costs
            new HashMap<Method, Integer>();

    public LibInfo(String manifest) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        this.manifest = (Manifest) Class.forName(manifest).newInstance();
        readEquivalentMethods();
        readEquivalentTypes();
        readMethodCosts();
        readTransformationCosts();
    }

    private void readEquivalentMethods()
    {
        for (Method m : manifest.getBaseMethods()) {
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
        for (JavaClass t : manifest.getBaseTypes()) {
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
                    cem.put(m, ((Cost)an).value());
    }

    private void readTransformationCosts()
    {
        for (Method m : manifest.getTransformations()) // TODO check that types are equivalent?
            for (Annotation an : AnnotationReader.getAnnotations(m.getAttributes()))
                if (an instanceof Cost)
                    ctt.put(m, ((Cost)an).value());
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        String n = String.format("%n");

        s.append("Equivalent methods:" + n);
        for (Method m : manifest.getBaseMethods()) {
            s.append("  " + m.getName() + " -> { ");
            for (Method e : em.get(m))
                s.append(e.getReturnType() + ":" + e.getName() + " "); // TODO fix this info
            s.append("}" + n);
        }

        s.append("Equivalent types:" + n);
        for (JavaClass t : manifest.getBaseTypes()) {
            s.append("  " + t.getClassName() + " -> { ");
            for (JavaClass e : et.get(t))
                s.append(e.getClassName() + " ");
            s.append("}" + n);
        }

        s.append("Method costs:" + n);
        for (Method m : cem.keySet())
            s.append("  " + m.getName() + " -> " + cem.get(m) + n);

        s.append("Transformation costs:" + n);
        for (Method m : ctt.keySet())
            s.append("  " + m.getName() + " -> " + ctt.get(m) + n);

        return s.toString();
    }

}
