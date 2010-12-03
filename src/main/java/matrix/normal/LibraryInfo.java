package matrix.normal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import static matrix.normal.Utils.*;

public class LibraryInfo
{
    public JavaClass BT;
    public List<JavaClass> TT;
    public Map<JavaClass, Method> MM;
    public List<Method> BM;

    
    public static LibraryInfo getMatrixLibraryInfo()
    {
        try 
        {
            return getMatrixLibraryInfo2();
        } catch (Exception e) {
        }
        return null;
    }
    public static LibraryInfo getMatrixLibraryInfo2() throws Exception
    {
        LibraryInfo z = new LibraryInfo();
        z.BT = rlk("matrix.normal.Matrix");
        z.TT = new ArrayList<JavaClass>();
        z.TT.add(rlk("matrix.normal.DefaultMatrix"));
        z.TT.add(rlk("matrix.normal.EigenDecompMatrix"));

        z.BM = new ArrayList<Method>();
        z.MM = new HashMap<JavaClass, Method>();
        z.BM.add(rlm("matrix.normal.MatrixOperations", "svd"));
        z.BM.add(rlm("matrix.normal.MatrixOperations", "power"));
        z.MM.put(z.TT.get(0), rlm("matrix.normal.DefaultMatrix", "svd"));
        z.MM.put(z.TT.get(0), rlm("matrix.normal.DefaultMatrix", "power"));
        z.MM.put(z.TT.get(1), rlm("matrix.normal.EigenDecompMatrix", "svd"));
        z.MM.put(z.TT.get(1), rlm("matrix.normal.EigenDecompMatrix", "power"));
        
        return z;
    }
}
