package optimizer.instrument;

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

}
