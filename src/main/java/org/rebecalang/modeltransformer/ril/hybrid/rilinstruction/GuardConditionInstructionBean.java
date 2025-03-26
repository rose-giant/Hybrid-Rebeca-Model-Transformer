package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class GuardConditionInstructionBean extends InstructionBean {
    String guardCondition;
    public GuardConditionInstructionBean(String computedModeName, String guardCondition) {
        super();
        this.guardCondition = guardCondition;
    }

    @Override
    public String toString() {
        return "guard = " + this.guardCondition;
    }
}
