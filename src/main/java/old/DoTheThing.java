package old;

import static optimizer.instrument.Utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import matrix.Manifest;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.util.InstructionFinder.CodeConstraint;

public class DoTheThing
{
    public Manifest lib;
    public Method m;
    public MethodGen mg;
    public ConstantPoolGen cpg; 
    public JavaClass c; 
    public static void main() throws Exception
    {
        DoTheThing dtt = new DoTheThing();
        dtt.lib = new Manifest();
        dtt.c = rlk("matrix.normal.App");
        dtt.m = rlm("matrix.normal.App", "proc01");
    }
    
    public void doStuff() throws Exception
    {
        cpg = new ConstantPoolGen(c.getConstantPool());
        mg = new MethodGen(m, c.getClassName(), cpg);
        
    }
    
    // get calls to base method calls
    public List<Method> getBC(MethodGen mg)
    {
        List<Method> BC = new ArrayList<Method>();
        
        InstructionList   il    = mg.getInstructionList();
        InstructionFinder f     = new InstructionFinder(il);
        String            pat   = "invokestatic";
        
        
        CodeConstraint constraint = new CodeConstraint() {
            public boolean checkCode(InstructionHandle[] match) {
              InvokeInstruction if1 = (InvokeInstruction)match[0].getInstruction();
              return lib.getBaseMethods().contains(if1.getMethodName(cpg));
            }  
          };
        for(Iterator<?> e = f.search(pat, constraint); e.hasNext(); ) {
            InstructionHandle[] match = (InstructionHandle[])e.next();
            match[0].getInstruction();
          }
        return BC;
    }
    
//    il.append(_factory.createInvoke("java.lang.Object", "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
}
