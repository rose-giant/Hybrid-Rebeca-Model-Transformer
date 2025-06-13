package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import java.util.Map;
import java.util.TreeMap;

public class SendMessageWithAfterInstructionBean extends InstructionBean {
    public SendMessageWithAfterInstructionBean(Variable receiver, String msgsrvName, Pair<Float, Float> afterInterval) {
        this(receiver, msgsrvName, new TreeMap<String, Object>(), afterInterval);
    }

    private Variable base;
    private String methodName;
    private TreeMap<String, Object> parameters;
    private Pair<Float, Float> arrivalInterval = null;

    public SendMessageWithAfterInstructionBean(Variable receiver, String msgsrvName,
                                               TreeMap<String, Object> stringObjectTreeMap, Pair<Float, Float> arrivalInterval) {
        this.base = receiver;
        this.methodName = msgsrvName;
        this.parameters = stringObjectTreeMap;
        this.arrivalInterval = arrivalInterval;
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

    public String getMethodName() {
        return methodName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Variable getBase() {
        return base;
    }

    public Pair<Float, Float> getArrivalInterval() {
        return arrivalInterval;
    }
}
