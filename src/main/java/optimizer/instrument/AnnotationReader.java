package optimizer.instrument;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.AttributeReader;
import org.apache.bcel.classfile.ConstantObject;
import org.apache.bcel.classfile.ConstantPool;

public class AnnotationReader implements AttributeReader {

    @Override
    public Attribute createAttribute(int index, int length, DataInputStream in, ConstantPool cp) {
        try {
            short numAnnotations = in.readShort();
            List<Map<String, Object>> a = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < numAnnotations; i++)
                a.add(readAnnotation(in, cp));
            return new AnnotationsAttribute(Constants.ATTR_UNKNOWN, index, length, cp, a);
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse annotations.", e);
        }
    }

    public static Annotation getAnnotation(Map<String, Object> map) {
        String typeName = (String)map.get("_type");
        typeName = typeName.substring(1, typeName.length()-1);
        typeName = typeName.replace("/", ".");
        try {
            Class<?> type = Class.forName(typeName);
            ClassLoader cl = AnnotationReader.class.getClassLoader();
            return (Annotation)Proxy.newProxyInstance(cl, new Class[] { type }, new MyInvocationHandler(typeName, map));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class: " + typeName, e);
        }
    }

    public static Collection<Annotation> getAnnotations(Attribute[] attributes) {
        Collection<Annotation> annotations = new ArrayList<Annotation>();
        for (Attribute a : attributes)
            if (a instanceof AnnotationsAttribute)
                for (Map<String, Object> m : ((AnnotationsAttribute)a).getAnnotations())
                    annotations.add(getAnnotation(m));
        return annotations;
    }

    private Map<String, Object> readAnnotation(DataInputStream in, ConstantPool cp) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        String type = cp.constantToString(cp.getConstant(in.readShort()));
        map.put("_type", type);
        short numMVPairs = in.readShort();
        for (int i = 0; i < numMVPairs; i++) {
            String name = cp.constantToString(cp.getConstant(in.readShort()));
            map.put(name, readMemberValue(in, cp));
        }
        return map;
    }

    private Object readMemberValue(DataInputStream in, ConstantPool cp) throws IOException {
        byte tag = in.readByte();
        switch (tag) {
        case 'B': case 'C': case 'D': case 'J': case 'F': case 'I': case 'S':
            return ((ConstantObject)cp.getConstant(in.readShort())).getConstantValue(cp);
        case 's':
            return cp.constantToString(cp.getConstant(in.readShort()));
        default:
            throw new UnsupportedOperationException("tag = " + tag);
        }
    }

    public static class MyInvocationHandler implements InvocationHandler {

        private String type;
        private Map<String, Object> map;

        public MyInvocationHandler(String type, Map<String, Object> map) {
            this.type = type;
            this.map = map;
        }

        public Object invoke(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();
            return methodName.equals("toString") ? "@" + type + map : map.get(methodName);
        }

    }

}
