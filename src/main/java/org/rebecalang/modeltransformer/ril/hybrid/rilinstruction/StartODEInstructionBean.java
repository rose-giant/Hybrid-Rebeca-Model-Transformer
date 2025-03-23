package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StartODEInstructionBean extends InstructionBean {
    String computedModeName;
    String ode;
    int odeIndex = 0;

    public void setOdeIndex(int odeIndex) {
        this.odeIndex = odeIndex;
    }

    public StartODEInstructionBean(String computedModeName, String ode) {
        super();
        this.computedModeName = computedModeName;
        this.ode = ode;
    }

    @Override
    public String toString() {
        return this.computedModeName + ".ode$"+ this.odeIndex +" = " + this.ode;
    }
}
