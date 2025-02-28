package org.rebecalang.modeltransformer.ril;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class RILModel {
	Hashtable<String, ArrayList<InstructionBean>> transformedRILModel;
	
	public RILModel() {
		transformedRILModel = new Hashtable<String, ArrayList<InstructionBean>>();
	}
	
	public void addMethod(String computedMethodName, ArrayList<InstructionBean> instructions) {
		transformedRILModel.put(computedMethodName, instructions);
	}

	public void addEnvVariable(String computedEnvVarName,ArrayList<InstructionBean> instructions) {
		transformedRILModel.put(computedEnvVarName, instructions);
	}

	public void addMode(String computedModeName, ArrayList<InstructionBean> instructions) {
		transformedRILModel.put(computedModeName, instructions);
	}

	public Set<String> getMethodNames() {
		return transformedRILModel.keySet();
	}

	public ArrayList<InstructionBean> getInstructionList(String methodName) {
		return transformedRILModel.get(methodName);
	}
}
