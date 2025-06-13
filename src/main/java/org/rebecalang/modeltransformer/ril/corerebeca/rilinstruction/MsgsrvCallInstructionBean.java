package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MsgsrvCallInstructionBean extends AbstractCallingInstructionBean {

	public MsgsrvCallInstructionBean(Variable receiver, String msgsrvName) {
		this(receiver, msgsrvName, new TreeMap<String, Object>());
	}

	public MsgsrvCallInstructionBean(Variable base, String methodName, Map<String, Object> parameters) {
		super(base, methodName, parameters);
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
		for (Entry<String, Object> entry : parameters.entrySet()) {
			string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
		}
		return string + ")";
	}
}
