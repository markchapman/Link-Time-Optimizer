package lto;

import java.lang.instrument.Instrumentation;
import org.apache.bcel.classfile.Attribute;
import lto.instrument.Transformer;
import lto.libinfo.AnnotationReader;
import lto.libinfo.LibInfo;

public class LTO
{
    public static void premain(String args, Instrumentation inst) throws Exception
    {
        LibInfo li = new LibInfo(args);
        inst.addTransformer(new Transformer(li));
    }
    
    static {
        Attribute.addAttributeReader("RuntimeVisibleAnnotations", new AnnotationReader());
        Attribute.addAttributeReader("RuntimeInvisibleAnnotations", new AnnotationReader());
    };
}
