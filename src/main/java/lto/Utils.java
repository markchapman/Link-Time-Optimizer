package lto;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

// cuz were lazy
public class Utils
{

    public static JavaClass rlk(String s)
    {
        try {
            return Repository.lookupClass(s);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class: " + s, e);
        }
    }

    // assume no overloading
    public static Method rlm(String sc, String sm)
    {
        for (Method m : rlk(sc).getMethods())
            if (m.getName().equals(sm))
                return m;
        return null;
    }
    
    public static Method rlm(String s)
    {
        int i = s.lastIndexOf('.');
        return rlm(s.substring(0, i), s.substring(i + 1));
    }
}
