package lto.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import lto.libinfo.LibInfo;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;

public class Transformer implements ClassFileTransformer 
{
    private LibInfo li;
    
    public Transformer(LibInfo li) {
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
