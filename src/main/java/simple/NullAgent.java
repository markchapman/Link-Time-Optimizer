package simple;

import java.lang.instrument.Instrumentation;

public class NullAgent {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new NullTransformer());
    }

}
