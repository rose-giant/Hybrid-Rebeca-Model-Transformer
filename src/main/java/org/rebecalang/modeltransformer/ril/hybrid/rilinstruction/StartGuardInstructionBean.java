package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StartGuardInstructionBean extends InstructionBean {
    String computedModeName;
    public StartGuardInstructionBean(String computedModeName) {
        super();
        this.computedModeName = computedModeName;
    }

    @Override
    public String toString() {
        return this.computedModeName + ".guard";
    }
}
