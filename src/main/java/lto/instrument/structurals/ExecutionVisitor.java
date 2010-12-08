/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *
 */ 
package lto.instrument.structurals;


import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.generic.*;

/**
 * This Visitor class may be used for a type-based Java Virtual Machine
 * simulation.
 * It does not check for correct types on the OperandStack or in the
 * LocalVariables; nor does it check their sizes are sufficiently big.
 * Thus, to use this Visitor for bytecode verifying, you have to make sure
 * externally that the type constraints of the Java Virtual Machine instructions
 * are satisfied. An InstConstraintVisitor may be used for this.
 * Anyway, this Visitor does not mandate it. For example, when you
 * visitIADD(IADD o), then there are two stack slots popped and one
 * stack slot containing a Type.INT is pushed (where you could also
 * pop only one slot if you know there are two Type.INT on top of the
 * stack). Monitor-specific behaviour is not simulated.
 * 
 * </P><B>Conventions:</B>
 *
 * Type.VOID will never be pushed onto the stack. Type.DOUBLE and Type.LONG
 * that would normally take up two stack slots (like Double_HIGH and
 * Double_LOW) are represented by a simple single Type.DOUBLE or Type.LONG
 * object on the stack here.
 * If a two-slot type is stored into a local variable, the next variable
 * is given the type Type.UNKNOWN.
 *
 * @version $Id: ExecutionVisitor.java 386056 2006-03-15 11:31:56Z tcurdt $
 * @author Enver Haase
 * @see #visitDSTORE(DSTORE o)
 * @see InstConstraintVisitor
 */
public class ExecutionVisitor extends EmptyVisitor implements Visitor{

	/**
	 * The executionframe we're operating on.
	 */
	private Frame frame = null;

	/**
	 * The ConstantPoolGen we're working with.
	 * @see #setConstantPoolGen(ConstantPoolGen)
	 */
	private ConstantPoolGen cpg = null;

	/**
	 * Constructor. Constructs a new instance of this class.
	 */
	public ExecutionVisitor(){}

	private OperandStack S;
    private LocalVariables L;

	/**
	 * Sets the ConstantPoolGen needed for symbolic execution.
	 */
	public void setConstantPoolGen(ConstantPoolGen cpg){
		this.cpg = cpg;
	}
    public int newVar(){ return frame.newVar(); }
	
	/**
	 * The only method granting access to the single instance of
	 * the ExecutionVisitor class. Before actively using this
	 * instance, <B>SET THE ConstantPoolGen FIRST</B>.
	 * @see #setConstantPoolGen(ConstantPoolGen)
	 */
	public void setFrame(Frame f){
		this.frame = f;
		this.S = f.getStack();
		this.L = f.getLocals();
	}

	///** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	//public void visitWIDE(WIDE o){
	// The WIDE instruction is modelled as a flag
	// of the embedded instructions in BCEL.
	// Therefore BCEL checks for possible errors
	// when parsing in the .class file: We don't
	// have even the possibilty to care for WIDE
	// here.
	//}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitAALOAD(AALOAD o){
		S.pop();														// pop the index int
//System.out.print(S.peek());
		Type t = S.pop(); // Pop Array type
		if (t == Type.NULL){
			S.push(Type.NULL);
		}	// Do nothing stackwise --- a NullPointerException is thrown at Run-Time
		else{
			ArrayType at = (ArrayType) t;	
			S.push(at.getElementType());
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitAASTORE(AASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitACONST_NULL(ACONST_NULL o){
		S.push(Type.NULL);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitALOAD(ALOAD o){
		S.push(L.get2(o.getIndex()));
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitANEWARRAY(ANEWARRAY o){
		S.pop(); //count
		S.push( new ArrayType(o.getType(cpg), 1) );
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitARETURN(ARETURN o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitARRAYLENGTH(ARRAYLENGTH o){
		S.pop();
		S.push(Type.INT);
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitASTORE(ASTORE o){
		L.set(o.getIndex(), S.pop2());
		//System.err.println("TODO-DEBUG:	set LV '"+o.getIndex()+"' to '"+L.get(o.getIndex())+"'.");
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitATHROW(ATHROW o){
		Type t = S.pop();
		S.clear();
		if (t.equals(Type.NULL)) {
            S.push(Type.getType("Ljava/lang/NullPointerException;"));
        } else {
            S.push(t);
        }
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitBALOAD(BALOAD o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitBASTORE(BASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitBIPUSH(BIPUSH o){
		S.push(Type.INT);
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitCALOAD(CALOAD o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitCASTORE(CASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitCHECKCAST(CHECKCAST o){
		// It's possibly wrong to do so, but SUN's
		// ByteCode verifier seems to do (only) this, too.
		// TODO: One could use a sophisticated analysis here to check
		//       if a type cannot possibly be cated to another and by
		//       so doing predict the ClassCastException at run-time.
		Integer x = (Integer)S.pop2()[1];
		S.push(o.getType(cpg), x);
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitD2F(D2F o){
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitD2I(D2I o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitD2L(D2L o){
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDADD(DADD o){
		S.pop();
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDALOAD(DALOAD o){
		S.pop();
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDASTORE(DASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDCMPG(DCMPG o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDCMPL(DCMPL o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDCONST(DCONST o){
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDDIV(DDIV o){
		S.pop();
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDLOAD(DLOAD o){
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDMUL(DMUL o){
		S.pop();
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDNEG(DNEG o){
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDREM(DREM o){
		S.pop();
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDRETURN(DRETURN o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDSTORE(DSTORE o){
		L.set(o.getIndex(), S.pop());
		L.set(o.getIndex()+1, Type.UNKNOWN);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDSUB(DSUB o){
		S.pop();
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDUP(DUP o){
		Object[] t = S.pop2();
		S.push(t);
		S.push(t);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDUP_X1(DUP_X1 o){
	    Object[] w1 = S.pop2();
	    Object[] w2 = S.pop2();
		S.push(w1);
		S.push(w2);
		S.push(w1);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDUP_X2(DUP_X2 o){
	    Object[] w1 = S.pop2();
	    Object[] w2 = S.pop2();
		if (((Type)w2[0]).getSize() == 2){
			S.push(w1);
			S.push(w2);
			S.push(w1);
		}
		else{
		    Object[] w3 = S.pop2();
			S.push(w1);
			S.push(w3);
			S.push(w2);
			S.push(w1);
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDUP2(DUP2 o){
	    Object[] t = S.pop2();
		if (((Type)t[0]).getSize() == 2){
			S.push(t);
			S.push(t);
		}
		else{ // ((Type)t[0]).getSize() is 1
		    Object[] u = S.pop2();
			S.push(u);
			S.push(t);
			S.push(u);
			S.push(t);
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDUP2_X1(DUP2_X1 o){
	    Object[] t = S.pop2();
		if (((Type)t[0]).getSize() == 2){
		    Object[] u = S.pop2();
			S.push(t);
			S.push(u);
			S.push(t);
		}
		else{ //((Type)t[0]).getSize() is1
		    Object[] u = S.pop2();
		    Object[] v = S.pop2();
			S.push(u);
			S.push(t);
			S.push(v);
			S.push(u);
			S.push(t);
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitDUP2_X2(DUP2_X2 o){
		Object[] t = S.pop2();
		if (((Type)t[0]).getSize() == 2){
			Object[] u = S.pop2();
			if (((Type)u[0]).getSize() == 2){
				S.push(t);
				S.push(u);
				S.push(t);
			}else{
				Object[] v = S.pop2();
				S.push(t);
				S.push(v);
				S.push(u);
				S.push(t);
			}
		}
		else{ //((Type)t[0]).getSize() is 1
			Object[] u = S.pop2();
			Object[] v = S.pop2();
			if (((Type)v[0]).getSize() == 2){
				S.push(u);
				S.push(t);
				S.push(v);
				S.push(u);
				S.push(t);
			}else{
				Object[] w = S.pop2();
				S.push(u);
				S.push(t);
				S.push(w);
				S.push(v);
				S.push(u);
				S.push(t);
			}
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitF2D(F2D o){
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitF2I(F2I o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitF2L(F2L o){
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFADD(FADD o){
		S.pop();
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFALOAD(FALOAD o){
		S.pop();
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFASTORE(FASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFCMPG(FCMPG o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFCMPL(FCMPL o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFCONST(FCONST o){
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFDIV(FDIV o){
		S.pop();
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFLOAD(FLOAD o){
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFMUL(FMUL o){
		S.pop();
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFNEG(FNEG o){
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFREM(FREM o){
		S.pop();
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFRETURN(FRETURN o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFSTORE(FSTORE o){
		L.set(o.getIndex(), S.pop());
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitFSUB(FSUB o){
		S.pop();
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitGETFIELD(GETFIELD o){
		S.pop();
		Type t = o.getFieldType(cpg);
		if (	t.equals(Type.BOOLEAN)	||
					t.equals(Type.CHAR)			||
					t.equals(Type.BYTE) 		||
					t.equals(Type.SHORT)		) {
            t = Type.INT;
        }
		S.push(t);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitGETSTATIC(GETSTATIC o){
		Type t = o.getFieldType(cpg);
		if (	t.equals(Type.BOOLEAN)	||
					t.equals(Type.CHAR)			||
					t.equals(Type.BYTE) 		||
					t.equals(Type.SHORT)		) {
            t = Type.INT;
        }
		S.push(t);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitGOTO(GOTO o){
		// no stack changes.
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitGOTO_W(GOTO_W o){
		// no stack changes.
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitI2B(I2B o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitI2C(I2C o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitI2D(I2D o){
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitI2F(I2F o){
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitI2L(I2L o){
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitI2S(I2S o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIADD(IADD o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIALOAD(IALOAD o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIAND(IAND o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIASTORE(IASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitICONST(ICONST o){
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIDIV(IDIV o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ACMPEQ(IF_ACMPEQ o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ACMPNE(IF_ACMPNE o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ICMPEQ(IF_ICMPEQ o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ICMPGE(IF_ICMPGE o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ICMPGT(IF_ICMPGT o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ICMPLE(IF_ICMPLE o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ICMPLT(IF_ICMPLT o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIF_ICMPNE(IF_ICMPNE o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFEQ(IFEQ o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFGE(IFGE o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFGT(IFGT o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFLE(IFLE o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFLT(IFLT o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFNE(IFNE o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFNONNULL(IFNONNULL o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIFNULL(IFNULL o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIINC(IINC o){
		// stack is not changed.
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitILOAD(ILOAD o){
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIMUL(IMUL o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitINEG(INEG o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitINSTANCEOF(INSTANCEOF o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitINVOKEINTERFACE(INVOKEINTERFACE o){
		S.pop();	//objectref
		for (int i=0; i<o.getArgumentTypes(cpg).length; i++){
			S.pop();
		}
		// We are sure the invoked method will xRETURN eventually
		// We simulate xRETURNs functionality here because we
		// don't really "jump into" and simulate the invoked
		// method.
		if (o.getReturnType(cpg) != Type.VOID){
			Type t = o.getReturnType(cpg);
			if (	t.equals(Type.BOOLEAN)	||
						t.equals(Type.CHAR)			||
						t.equals(Type.BYTE) 		||
						t.equals(Type.SHORT)		) {
                t = Type.INT;
            }
			S.push(t, newVar());
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitINVOKESPECIAL(INVOKESPECIAL o){
		if (o.getMethodName(cpg).equals(Constants.CONSTRUCTOR_NAME)){
			UninitializedObjectType t = (UninitializedObjectType) S.peek(o.getArgumentTypes(cpg).length);
			if (t == Frame._this){	
				Frame._this = null;
			}
			S.initializeObject(t);
			L.initializeObject(t);
		}
		S.pop();	//objectref
		for (int i=0; i<o.getArgumentTypes(cpg).length; i++){
			S.pop();
		}
		// We are sure the invoked method will xRETURN eventually
		// We simulate xRETURNs functionality here because we
		// don't really "jump into" and simulate the invoked
		// method.
		if (o.getReturnType(cpg) != Type.VOID){
			Type t = o.getReturnType(cpg);
			if (	t.equals(Type.BOOLEAN)	||
						t.equals(Type.CHAR)			||
						t.equals(Type.BYTE) 		||
						t.equals(Type.SHORT)		) {
                t = Type.INT;
            }
			S.push(t, newVar());
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitINVOKESTATIC(INVOKESTATIC o){
		for (int i=0; i<o.getArgumentTypes(cpg).length; i++)
		{
			Object[] z = S.pop2();
			int x = (Integer)z[1];
		}
		
		// We are sure the invoked method will xRETURN eventually
		// We simulate xRETURNs functionality here because we
		// don't really "jump into" and simulate the invoked
		// method.
		if (o.getReturnType(cpg) != Type.VOID){
			Type t = o.getReturnType(cpg);
			if (	t.equals(Type.BOOLEAN)	||
						t.equals(Type.CHAR)			||
						t.equals(Type.BYTE) 		||
						t.equals(Type.SHORT)		) {
                t = Type.INT;
            }
			S.push(t, newVar());
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL o){
		S.pop(); //objectref
		for (int i=0; i<o.getArgumentTypes(cpg).length; i++){
			S.pop();
		}
		// We are sure the invoked method will xRETURN eventually
		// We simulate xRETURNs functionality here because we
		// don't really "jump into" and simulate the invoked
		// method.
		if (o.getReturnType(cpg) != Type.VOID){
			Type t = o.getReturnType(cpg);
			if (	t.equals(Type.BOOLEAN)	||
						t.equals(Type.CHAR)			||
						t.equals(Type.BYTE) 		||
						t.equals(Type.SHORT)		) {
                t = Type.INT;
            }
			S.push(t, newVar());
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIOR(IOR o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIREM(IREM o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIRETURN(IRETURN o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitISHL(ISHL o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitISHR(ISHR o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitISTORE(ISTORE o){
		L.set(o.getIndex(), S.pop());
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitISUB(ISUB o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIUSHR(IUSHR o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitIXOR(IXOR o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitJSR(JSR o){
		S.push(new ReturnaddressType(o.physicalSuccessor()));
//System.err.println("TODO-----------:"+o.physicalSuccessor());
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitJSR_W(JSR_W o){
		S.push(new ReturnaddressType(o.physicalSuccessor()));
	}

	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitL2D(L2D o){
		S.pop();
		S.push(Type.DOUBLE);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitL2F(L2F o){
		S.pop();
		S.push(Type.FLOAT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitL2I(L2I o){
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLADD(LADD o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLALOAD(LALOAD o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLAND(LAND o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLASTORE(LASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLCMP(LCMP o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLCONST(LCONST o){
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLDC(LDC o){
		Constant c = cpg.getConstant(o.getIndex());
		if (c instanceof ConstantInteger){
			S.push(Type.INT);
		}
		if (c instanceof ConstantFloat){
			S.push(Type.FLOAT);
		}
		if (c instanceof ConstantString){
			S.push(Type.STRING);
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLDC_W(LDC_W o){
		Constant c = cpg.getConstant(o.getIndex());
		if (c instanceof ConstantInteger){
			S.push(Type.INT);
		}
		if (c instanceof ConstantFloat){
			S.push(Type.FLOAT);
		}
		if (c instanceof ConstantString){
			S.push(Type.STRING);
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLDC2_W(LDC2_W o){
		Constant c = cpg.getConstant(o.getIndex());
		if (c instanceof ConstantLong){
			S.push(Type.LONG);
		}
		if (c instanceof ConstantDouble){
			S.push(Type.DOUBLE);
		}
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLDIV(LDIV o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLLOAD(LLOAD o){
		S.push(L.get(o.getIndex()));
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLMUL(LMUL o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLNEG(LNEG o){
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLOOKUPSWITCH(LOOKUPSWITCH o){
		S.pop(); //key
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLOR(LOR o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLREM(LREM o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLRETURN(LRETURN o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLSHL(LSHL o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLSHR(LSHR o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLSTORE(LSTORE o){
		L.set(o.getIndex(), S.pop());
		L.set(o.getIndex()+1, Type.UNKNOWN);		
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLSUB(LSUB o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLUSHR(LUSHR o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitLXOR(LXOR o){
		S.pop();
		S.pop();
		S.push(Type.LONG);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitMONITORENTER(MONITORENTER o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitMONITOREXIT(MONITOREXIT o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitMULTIANEWARRAY(MULTIANEWARRAY o){
		for (int i=0; i<o.getDimensions(); i++){
			S.pop();
		}
		S.push(o.getType(cpg));
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitNEW(NEW o){
		S.push(new UninitializedObjectType((ObjectType) (o.getType(cpg))), newVar());
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitNEWARRAY(NEWARRAY o){
		S.pop();
		S.push(o.getType());
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitNOP(NOP o){
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitPOP(POP o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitPOP2(POP2 o){
		Type t = S.pop();
		if (t.getSize() == 1){
			S.pop();
		}		
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitPUTFIELD(PUTFIELD o){
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitPUTSTATIC(PUTSTATIC o){
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitRET(RET o){
		// do nothing, return address
		// is in in the local variables.
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitRETURN(RETURN o){
		// do nothing.
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitSALOAD(SALOAD o){
		S.pop();
		S.pop();
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitSASTORE(SASTORE o){
		S.pop();
		S.pop();
		S.pop();
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitSIPUSH(SIPUSH o){
		S.push(Type.INT);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitSWAP(SWAP o){
		Object[] t = S.pop2();
		Object[] u = S.pop2();
		S.push(t);
		S.push(u);
	}
	/** Symbolically executes the corresponding Java Virtual Machine instruction. */ 
	public void visitTABLESWITCH(TABLESWITCH o){
		S.pop();
	}
}
