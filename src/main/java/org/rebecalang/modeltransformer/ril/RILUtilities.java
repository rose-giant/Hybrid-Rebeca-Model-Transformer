package org.rebecalang.modeltransformer.ril;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.InvariantDeclaration;
import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.ModeDeclaration;
import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.PhysicalClassDeclaration;

public class RILUtilities {

	private static String computeModeName(String className, String modeName) {
		String canonicalModeName = className + ".mode." + modeName;
		return  canonicalModeName;
	}

	public static String computeModeName(PhysicalClassDeclaration pcd, ModeDeclaration md) {
		return computeModeName(pcd.getName(), md.getName());
	}

	private static String computeMethodName(String className, String methodName, List<Type> parametersTypes) {
		String canonicalMethodName = className + "." + methodName;
		canonicalMethodName += createListOfParameterTypes(parametersTypes);
		return canonicalMethodName;
	}

	private static String createListOfParameterTypes(List<Type> parametersTypes) {
		String paramTypes = "";
		for (Type parameterType : parametersTypes) {
			paramTypes += "$" + parameterType.getTypeName();
		}
		return paramTypes;
	}

	public static String computeMethodName(ReactiveClassDeclaration rcd, MethodDeclaration md) {
		List<FormalParameterDeclaration> parameters = md.getFormalParameters();
		List<Type> parametersType = new LinkedList<Type>();
		for (FormalParameterDeclaration fpd : parameters) {
			parametersType.add(fpd.getType());
		}
		return computeMethodName(rcd.getName(), md.getName(), parametersType);
	}

	public static String computeMethodName(Type baseType, MethodInSymbolTableSpecifier mssp) {
		List<Type> argumentsTypes = mssp.getArgumentsTypes();
		return computeMethodName(baseType.getTypeName(), mssp.getName(), argumentsTypes);
	}

	public static String computeMethodName(MethodInSymbolTableSpecifier castableMethodSpecification) {
		return castableMethodSpecification.getName() + 
				createListOfParameterTypes(castableMethodSpecification.getArgumentsTypes());
	}

}
