package lto.libinfo;

import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

@SuppressWarnings("serial")
public class AnnotationsAttribute extends Attribute {

    private List<Map<String, Object>> annotations;

    public AnnotationsAttribute(byte tag, int index, int length, ConstantPool cp,
            List<Map<String, Object>> annotations) {
        super(tag, index, length, cp);
        this.annotations = annotations;
    }

    @Override
    public void accept(Visitor v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attribute copy(ConstantPool cp) {
        return this;
    }

    public List<Map<String, Object>> getAnnotations() {
        return annotations;
    }

    @Override
    public String toString() {
        return annotations.toString();
    }

    /*
    public void dump (DataOutputStream out) throws IOException {
        ConstantPoolGen cpg = new ConstantPoolGen(getConstantPool());
        out.writeShort(getNameIndex());
        out.writeInt(getLength());
        out.writeShort(annotations.size());
        for (Map<String, ?> m : annotations) {
            out.writeShort(cpg.lookupClass((String)m.get("_type")));
            out.writeShort(m.size() - 1);
            for (String name : m.keySet()) {
                if (!name.equals("_type")) {
                    out.writeShort(cpg.lookupUtf8(name));
                    Object value = m.get(name);
                    if (value instanceof String) {
                        out.writeByte('s');
                        out.writeShort(cpg.lookupUtf8((String)value));
                    } else {
                        throw new UnsupportedOperationException("writing " + value + " (" + value.getClass() + ")");
                    }
                }
            }
        }
    }
    */

}
