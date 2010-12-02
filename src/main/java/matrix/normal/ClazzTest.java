package matrix.normal;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class ClazzTest
{
    public static void main(String[] args) throws Exception
    {
        JavaClass clazz = Repository.lookupClass("matrix.normal.App");
        System.out.println(clazz);
        printCode(clazz.getMethods());
    }

    public static void printCode(Method[] methods)
    {
        for (int i = 0; i < methods.length; i++)
        {
            System.out.println(methods[i]);

            Code code = methods[i].getCode();
            if (code != null) // Non-abstract method
                System.out.println(code);
        }
    }
}
