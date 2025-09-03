package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AbstractCallingInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import java.util.Map;

public class MsgsrvCallWithAfterInstructionBean extends AbstractCallingInstructionBean {
    private Pair<Object, Object> arrivalInterval = null;

    public MsgsrvCallWithAfterInstructionBean(Variable base, String methodName, Map<String, Object> parameters, Pair<Object, Object> arrivalInterval) {
        super(base, methodName, parameters);
        this.arrivalInterval = arrivalInterval;
    }

    @Override
    public String getMethodName() {
        return super.getMethodName();
    }

    @Override
    public Map<String, Object> getParameters() {
        return super.getParameters();
    }

    @Override
    public Variable getBase() {
        return super.getBase();
    }

    @Override
    public String toString() {
        String string = base + "." + methodName + "( ";
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
        }

        if (arrivalInterval != null) {
            return string + ")" + " after(lower ->" + arrivalInterval.getFirst() + ", upper -> " + arrivalInterval.getSecond() + ")";
        }
        return string + ")";
    }

    public Pair<Object, Object> getArrivalInterval() {
        return arrivalInterval;
    }
}
