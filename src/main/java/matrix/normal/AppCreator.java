package matrix.normal;

import org.apache.bcel.generic.*;
import org.apache.bcel.*;
import java.io.*;

public class AppCreator implements Constants {

    private InstructionFactory _factory;
    private ConstantPoolGen    _cp;
    private ClassGen           _cg;

    public AppCreator() {
        _cg = new ClassGen("matrix.normal.App", "java.lang.Object", "App.java", ACC_PUBLIC | ACC_SUPER,
                new String[] {  });

        _cp = _cg.getConstantPool();
        _factory = new InstructionFactory(_cg, _cp);
    }

    public void create(OutputStream out) throws IOException {
        createMethod_0();
        createMethod_1();
        createMethod_2();
        _cg.getJavaClass().dump(out);
    }

    private void createMethod_0() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(ACC_PUBLIC, Type.VOID, Type.NO_ARGS, new String[] {  }, "<init>",
                "matrix.normal.App", il, _cp);

        InstructionHandle ih_0 = il.append(_factory.createLoad(Type.OBJECT, 0));
        il.append(_factory.createInvoke("java.lang.Object", "<init>", Type.VOID, Type.NO_ARGS,
                Constants.INVOKESPECIAL));
        InstructionHandle ih_4 = il.append(_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }

    private void createMethod_1() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(ACC_PUBLIC | ACC_STATIC, Type.VOID,
                new Type[] { new ArrayType(Type.STRING, 1) }, new String[] { "arg0" }, "main", "matrix.normal.App", il,
                _cp);

        InstructionHandle ih_0 = il.append(new PUSH(_cp, 2));
        il.append(_factory.createNewArray(Type.DOUBLE, (short) 1));
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(_cp, 0));
        il.append(new PUSH(_cp, 2));
        il.append(_factory.createNewArray(Type.DOUBLE, (short) 1));
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(_cp, 0));
        il.append(new PUSH(_cp, 1.0));
        il.append(InstructionConstants.DASTORE);
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(_cp, 1));
        il.append(new PUSH(_cp, 2.0));
        il.append(InstructionConstants.DASTORE);
        il.append(InstructionConstants.AASTORE);
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(_cp, 1));
        il.append(new PUSH(_cp, 2));
        il.append(_factory.createNewArray(Type.DOUBLE, (short) 1));
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(_cp, 0));
        il.append(new PUSH(_cp, 3.0));
        il.append(InstructionConstants.DASTORE);
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(_cp, 1));
        il.append(new PUSH(_cp, 4.0));
        il.append(InstructionConstants.DASTORE);
        il.append(InstructionConstants.AASTORE);
        il.append(_factory.createInvoke("matrix.normal.MatrixFactory", "create",
                new ObjectType("matrix.normal.Matrix"), new Type[] { new ArrayType(Type.DOUBLE, 2) },
                Constants.INVOKESTATIC));
        il.append(_factory.createStore(Type.OBJECT, 1));
        InstructionHandle ih_42 = il.append(_factory.createLoad(Type.OBJECT, 1));
        il.append(new PUSH(_cp, 4));
        il.append(_factory.createInvoke("matrix.normal.App", "op", Type.VOID,
                new Type[] { new ObjectType("matrix.normal.Matrix"), Type.INT }, Constants.INVOKESTATIC));
        InstructionHandle ih_47 = il.append(_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }

    private void createMethod_2() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(ACC_PUBLIC | ACC_STATIC, Type.VOID,
                new Type[] { new ObjectType("matrix.normal.Matrix"), Type.INT }, new String[] { "arg0", "arg1" }, "op",
                "matrix.normal.App", il, _cp);

        InstructionHandle ih_0 = il.append(_factory.createLoad(Type.OBJECT, 0));
        il.append(_factory.createLoad(Type.INT, 1));
        il.append(_factory.createInvoke("matrix.normal.MatrixOperations", "power",
                new ObjectType("matrix.normal.Matrix"),
                new Type[] { new ObjectType("matrix.normal.Matrix"), Type.INT }, Constants.INVOKESTATIC));
        il.append(InstructionConstants.POP);
        InstructionHandle ih_6 = il.append(_factory.createLoad(Type.OBJECT, 0));
        il.append(_factory.createInvoke("matrix.normal.MatrixOperations", "eigenvalues",
                new ObjectType("matrix.normal.Matrix"), new Type[] { new ObjectType("matrix.normal.Matrix") },
                Constants.INVOKESTATIC));
        il.append(InstructionConstants.POP);
        InstructionHandle ih_11 = il.append(_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }

    public static void main(String[] args) throws Exception {
        matrix.normal.AppCreator creator = new matrix.normal.AppCreator();
        creator.create(new FileOutputStream("matrix.normal.App.class"));
    }

}
