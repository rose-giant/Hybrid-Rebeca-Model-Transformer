package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class ContnuousNonDetInstructionBean extends InstructionBean {
    private Object lowerBound;
    private Object upperBound;
    private Object assignee;

    public Object getAssignee() {
        return assignee;
    }

    public Object getLowerBound() {
        return lowerBound;
    }

    public Object getUpperBound() {
        return upperBound;
    }

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

