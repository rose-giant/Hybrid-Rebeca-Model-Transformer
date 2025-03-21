package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StartODEInstructionBean extends InstructionBean {
    String computedModeName;
    String ode;
    public StartODEInstructionBean(String computedModeName, String ode) {
        super();
        this.computedModeName = computedModeName;
        this.ode = ode;
    }

    @Override
    public String toString() {
        return this.computedModeName + ".ode = " + this.ode;
    }
}
