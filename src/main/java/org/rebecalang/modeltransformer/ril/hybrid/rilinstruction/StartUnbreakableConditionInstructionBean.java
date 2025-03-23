package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.lang.reflect.Constructor;

public class StartUnbreakableConditionInstructionBean extends InstructionBean {

    String unbreakableCondition;

    public StartUnbreakableConditionInstructionBean(String unbreakableCondition) {
        this.unbreakableCondition = unbreakableCondition;
    }

    @Override
    public String toString() {
        return this.unbreakableCondition;
    }
}
