package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StartInvariantInstructionBean extends InstructionBean {

    String computedModeName;
    String invariantCondition;
    public StartInvariantInstructionBean(String computedModeName, String invariantCondition) {
        super();
        this.computedModeName = computedModeName;
        this.invariantCondition = invariantCondition;
    }

    @Override
    public String toString() {
        return this.computedModeName + ".inv = " + this.invariantCondition;
    }
}
