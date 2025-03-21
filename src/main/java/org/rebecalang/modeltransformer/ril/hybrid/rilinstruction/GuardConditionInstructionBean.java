package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class GuardConditionInstructionBean extends InstructionBean {
    String computedModeName;
    String guardCondition;
    public GuardConditionInstructionBean(String computedModeName, String guardCondition) {
        super();
        this.computedModeName = computedModeName;
        this.guardCondition = guardCondition;
    }

    @Override
    public String toString() {
        return this.computedModeName + ".guard = " + this.guardCondition;
    }
}
