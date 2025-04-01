package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class ContnuousNonDetInstructionBean extends InstructionBean {
    Object lowerBound;
    Object upperBound;
    Object assignee;
    public ContnuousNonDetInstructionBean(Object assignee, Object lowerBound, Object upperBound) {
        this.assignee = assignee;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return assignee + " = ContinuousNonDet {" + lowerBound + ", " + upperBound + "}";
    }
}

