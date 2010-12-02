package matrix.normal;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

// cuz were lazy
public class Utils
{
    public static JavaClass rlk(String s) throws Exception
    {

        return Repository.lookupClass(s);
    }
    
    // assume no overloading
    public static Method rlm(String sc, String sm) throws Exception
    {
        Method[] mm = rlk(sc).getMethods();
        for (int i = 0; i < mm.length; i++)
            if (mm[i].getName().equals(sm))
                return mm[i];
        return null;
    }
}
