package lto.libinfo;

import static lto.Utils.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

public class LibInfo {

    private final Manifest manifest;
    public final Map<Method, Set<Method>>            ELM = 
            new HashMap<Method, Set<Method>>();
    public final Map<Method, Map<String, Method>> VLM = 
        new HashMap<Method, Map<String, Method>>();
    public final Map<JavaClass, Set<JavaClass>>      ET = 
            new HashMap<JavaClass, Set<JavaClass>>();
    public final Map<JavaClass, JavaClass>      ETinv = 
        new HashMap<JavaClass, JavaClass>();
    public final Map<Method, Method>                CLM = 
            new HashMap<Method, Method>();
    public final Map<Method, Method>                CLS = 
            new HashMap<Method, Method>();
    public final Map<JavaClass, Map<JavaClass, Method>> FLS = 
        new HashMap<JavaClass, Map<JavaClass, Method>>();
    public final Set<JavaClass> BT = new HashSet<JavaClass>();
    public final Set<Method> BLM = new HashSet<Method>();
    public final Set<Method> LS = new HashSet<Method>();
    
    //v(LM) : (ret(LM) + args(LM)) -> T
    //BT: BT -> {0, 1}            base types
    //BLM: BLM -> {0, 1}          base methods
    //LS: LS -> {0, 1}            type transforms
    //ELM: BLM -> LM -> {0, 1}    equivalent methods
    //VLM: BLM -> Vsig -> LM
    //ET:  BT -> T                equivalents types
    //ETinv: T -> BT
    //FLS:  T -> T -> LS
    //CLM: LM -> ZC               method costs
    //CLS: S -> ZC                transformation costs
    
    //CS:  S -> Z
    //CM:  M -> X -> Z

    public LibInfo(String manifest) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        this.manifest = (Manifest) Class.forName(manifest).newInstance();
        try{
        setup();
        } catch (Exception e) { System.out.println(e); e.printStackTrace(); System.exit(1);}
    }

    private void setup() throws Exception
    {
        // populate BT
        BT.addAll(manifest.getBaseTypes());

        // populate BLM
        BLM.addAll(manifest.getBaseMethods());
        
        // populate BLS
        LS.addAll(manifest.getTransformations());
        
        // populate ELM
        for (Method blm : BLM)
            for (Annotation an : AnnotationReader.getAnnotations(blm.getAttributes())) {
                if (an instanceof Equivalents) {
                    Set<Method> lms = new HashSet<Method>();
                    ELM.put(blm, lms);
                    for (String lm_name : ((Equivalents) an).value().split(","))
                        lms.add(rlm(lm_name));
                }
            }
        
        // populate ET, ETinv
        for (JavaClass bt : BT)
            for (Annotation an : AnnotationReader.getAnnotations(bt.getAttributes()))
                if (an instanceof Equivalents) {
                    Set<JavaClass> tset = new HashSet<JavaClass>();
                    ET.put(bt, tset);
                    for (String t_name : ((Equivalents) an).value().split(","))
                        tset.add(rlk(t_name));
                    for (JavaClass t : tset)
                    {
                        if (ETinv.containsKey(t))
                            throw new InvalidLibraryException("Bad type " + t + ": already equivalent to base method " + ETinv.get(t) + ".");
                        ETinv.put(t, bt);
                    }
                }
        
        // populate VLM
        for (Method blm : BLM)
        {
            VLM.put(blm, new HashMap<String, Method>());
            for (Method lm : ELM.get(blm))
            {
                String vsig = lm.getSignature();
                VLM.get(blm).put(vsig, lm);
            }
        }
        
        // VLM type check
        for (Method blm : BLM)
        {
            Map<Integer, JavaClass> bv = method2v(blm);
            for (Method lm : ELM.get(blm))
            {
                Map<Integer, JavaClass> v = method2v(lm);
                if (!v.keySet().equals(bv.keySet()))
                    throw new InvalidLibraryException("Bad method " + lm + ": number of parameters differ from base method.");
                for (int i : v.keySet())
                    if (!ETinv.get(v.get(i)).equals(bv.get(i)))
                        throw new InvalidLibraryException("Bad method " + lm + ": argument " + i + " is incompatible with base method.");
            }
        }
        
        // populate FLS
        for (JavaClass t : ETinv.keySet())
            FLS.put(t, new HashMap<JavaClass, Method>());
        for (Method ls : LS)
        {
            Map<Integer, JavaClass> v = method2v(ls);
            if (!v.containsKey(-1))
                throw new InvalidLibraryException("Bad transform " + ls + ": must return an object.");
            if (v.size() != 2)
                throw new InvalidLibraryException("Bad transform " + ls + ": must take exactly one argument.");
            JavaClass tu = v.get(-1);
            JavaClass tv = v.get(0);
            if (!ETinv.containsKey(tu))
                throw new InvalidLibraryException("Bad transform " + ls + ": invalid source type " + tu + ".");
            if (!ETinv.containsKey(tv))
                throw new InvalidLibraryException("Bad transform " + ls + ": invalid dest type " + tu + ".");
            if (!ETinv.get(tu).equals(ETinv.get(tv)))
                throw new InvalidLibraryException("Bad transform " + ls + ": source and dest types not compatible.");
            FLS.get(tu).put(tv, ls);
        }
        
        // populate CLM
        for (Set<Method> lms : ELM.values())
            for (Method lm : lms)
                for (Annotation an : AnnotationReader.getAnnotations(lm.getAttributes()))
                    /*
                    if (an instanceof Cost)
                        czm.put(zm, ((Cost)an).value());
                        */
                    if (an instanceof CostFn)
                    {
                        String zc_name = ((CostFn) an).value();
                        Method zc = rlm(zc_name);
                        CLM.put(lm, zc);
                    }
        
        // populate CLS
        for (Method ls : LS) // TODO check that types are equivalent?
            for (Annotation an : AnnotationReader.getAnnotations(ls.getAttributes()))
                /*
                if (an instanceof Cost)
                    czs.put(zs, ((Cost)an).value());
                    */
                if (an instanceof CostFn)
                {
                    String zc_name = ((CostFn) an).value();
                    Method zc = rlm(zc_name);
                    CLS.put(ls, zc);
                }
    }
    

    /* Converts BCEL Types to BCEL JavaClass. Checks that type is an object. */
    public static Map<Integer, JavaClass> method2v(Method m)
    {
        Map<Integer, JavaClass> v = new HashMap<Integer, JavaClass>();
        Type[] ta = m.getArgumentTypes();
        Type tr = m.getReturnType();
        {
            Type t = tr;
            if (t instanceof ObjectType)
            {
                String sc = ((ObjectType)t).getClassName();
                v.put(-1, rlk(sc));
            }
        }
        for (int i = 0; i < ta.length; i++)
        {
            Type t = ta[i];
            if (!(t instanceof ObjectType))
                throw new InvalidLibraryException("Bad method " + m + ": Argument type " + i + " is not an object.");
            String sc = ((ObjectType)t).getClassName();
            v.put(i, rlk(sc));
        }
        return v;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        String n = String.format("%n");

        s.append("Equivalent methods:" + n);
        for (Method bzm : manifest.getBaseMethods()) {
            s.append("  " + bzm.getName() + " -> { ");
            for (Method zm : ELM.get(bzm))
                s.append(zm.getReturnType() + ":" + zm.getName() + " "); // TODO fix this info
            s.append("}" + n);
        }

        s.append("Equivalent types:" + n);
        for (JavaClass bzt : manifest.getBaseTypes()) {
            s.append("  " + bzt.getClassName() + " -> { ");
            for (JavaClass zt : ET.get(bzt))
                s.append(zt.getClassName() + " ");
            s.append("}" + n);
        }

        s.append("Method costs:" + n);
        for (Method zm : CLM.keySet())
            s.append("  " + zm.getName() + " -> " + CLM.get(zm).getName() + n);

        s.append("Transformation costs:" + n);
        for (Method zs : CLS.keySet())
            s.append("  " + zs.getName() + " -> " + CLS.get(zs).getName() + n);

        return s.toString();
    }

}
