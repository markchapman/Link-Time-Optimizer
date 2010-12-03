package simple;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class NullTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader cl, String name, Class<?> type, ProtectionDomain domain, byte[] code)
            throws IllegalClassFormatException {
        System.out.println("Transformer to Transform Class: " + name);
        return code;
    }

}
