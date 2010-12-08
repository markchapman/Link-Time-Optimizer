package lto.instrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lto.libinfo.LibInfo;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.util.InstructionFinder.CodeConstraint;
import static lto.Utils.*;

public class MethodTransformer
{
    public LibInfo li;
    public JavaClass jc;
    public Method jm;
    public MethodGen mg;
    public ConstantPoolGen cpg;

    public MethodTransformer(JavaClass jc, Method jm, LibInfo li)
    {
        this.jc = jc;
        this.jm = jm;
        this.li = li;
    }

    public void doStuff() throws Exception
    {
        cpg = new ConstantPoolGen(jc.getConstantPool());
        mg = new MethodGen(jm, jc.getClassName(), cpg);
        Map<Integer, Method> BM = getBM();
        Map<Integer, Set<Integer>> BMV;
    }

    // get calls to base method calls
    public Map<Integer, Method> getBM()
    {
        Map<Integer, Method> BM = new HashMap<Integer, Method>();

        InstructionList il = mg.getInstructionList();
        InstructionFinder f = new InstructionFinder(il);
        String pat = "invokestatic";

        CodeConstraint constraint = new CodeConstraint() {
            public boolean checkCode(InstructionHandle[] match)
            {
                InvokeInstruction iv0 = (InvokeInstruction) match[0].getInstruction();
                return li.BLM.contains(iv0.getMethodName(cpg));
            }
        };
        
        for (Iterator<?> e = f.search(pat, constraint); e.hasNext();)
        {
            InstructionHandle[] match = (InstructionHandle[]) e.next();
            InvokeInstruction iv0 = (InvokeInstruction) match[0].getInstruction();
            BM.put(match[0].getPosition(), rlm(iv0.getMethodName(cpg)));
        }
        return BM;
    }

    // il.append(_factory.createInvoke("java.lang.Object", "<init>", Type.VOID,
    // Type.NO_ARGS, Constants.INVOKESPECIAL));
}
