package org.rebecalang.modeltransformer.ros.timedrebeca;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BaseClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InterfaceDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableInitializer;
import org.rebecalang.compiler.modelcompiler.timedrebeca.TimedRebecaTypeSystem;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.StatementTransformingException;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSTypesUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/* create the content of launch file */
@Component
public class MainTransformer{
	private final static String NEW_LINE = "\r\n";
	private final static String TAB = "\t";
	public final static String QUOTE_MARK = "\"";
	public final static String SEMICOLON = ";";
	
	@Autowired
	TimedRebeca2ROSExpressionTransformer expressionTransformer;
	
	@Autowired
	ExceptionContainer exceptionContainer;
	
	@Autowired
	TimedRebecaTypeSystem timedRebecaTypeSystem;
	
	@Autowired
	ReactiveClassTransformer reactiveClassTransformer;
	
	
	
	public String getLaunchFileContent(RebecaModel rebecaModel, String modelName) {
		String launchFileContent = "<launch>" + NEW_LINE;
		launchFileContent += getNodesDeclaration(rebecaModel, modelName) + getParamsDeclarations(rebecaModel);
		launchFileContent += NEW_LINE + "</launch>";
		return launchFileContent;
	}


	private String getNodesDeclaration(RebecaModel rebecaModel, String modelName) {
		String retValue = "";
		MainDeclaration rebecaMain = rebecaModel.getRebecaCode().getMainDeclaration();
		
		/* launch the nodes, based on rebec definitions */
		for(MainRebecDefinition rebecDefinition :rebecaMain.getMainRebecDefinition()) {
			ReactiveClassDeclaration itsClass = null;
			try {
				//As the given models are compiled and verified, this statements never throws exception 
				BaseClassDeclaration baseClassDeclaration = timedRebecaTypeSystem.getMetaData(rebecDefinition.getType());
				if(baseClassDeclaration instanceof InterfaceDeclaration)
					throw new StatementTransformingException("Rebeca to ROS transformer does not support interface definition", 
							baseClassDeclaration.getLineNumber(), baseClassDeclaration.getCharacter());
				itsClass = (ReactiveClassDeclaration) baseClassDeclaration;
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
			if(! itsClass.getAnnotations().isEmpty())
				break;
			retValue += "<node";
			retValue += " pkg=" + QUOTE_MARK + modelName + QUOTE_MARK; /* The argument pkg points to the package associated with the node that is to be launched, 
			 														we name the ROS package same as model name*/
			
			retValue += " type=" + QUOTE_MARK + itsClass.getName()  + QUOTE_MARK; /* type refers to the name of executable file */

			retValue += " name=" + QUOTE_MARK + rebecDefinition.getName() + "_node" + QUOTE_MARK; /* type refers to the name of node */
			retValue += ">" + NEW_LINE;
			
			retValue += "<param " + " name=" + QUOTE_MARK + "sender" + QUOTE_MARK +
					" type=" + QUOTE_MARK + "str" + QUOTE_MARK + 
					" value=" + QUOTE_MARK + rebecDefinition.getName() + QUOTE_MARK + "/>" + NEW_LINE;
			
			/* add the rebecs initial values as the parameters to the nodes */
			int i = 0;
			for(Expression arg : rebecDefinition.getArguments()) {
				FormalParameterDeclaration correspondentConstructorParam = itsClass.getConstructors().get(0).getFormalParameters().get(i);
				retValue += TAB + "<param " + " name=" + QUOTE_MARK + correspondentConstructorParam.getName() + QUOTE_MARK;
				String type = getLaunchFileFieldsTypes(correspondentConstructorParam.getType());
				retValue += " type=" + QUOTE_MARK + type + QUOTE_MARK;

				retValue += " value=" + QUOTE_MARK + expressionTransformer.translate(arg) + QUOTE_MARK;
				retValue += "/>" + NEW_LINE;
				i++;
			}
			
		
			retValue += getRemappingArguments(rebecaModel, rebecDefinition, modelName);
			retValue += "</node>" + NEW_LINE;
		}
		return retValue;
	}
	
	
	private String getRemappingArguments(RebecaModel rebecaModel, MainRebecDefinition rebecDefinition, String modelName) {
		String retValue = "";
		 ReactiveClassDeclaration itsClass = null;
		// itsClass = TypesUtilities.getInstance().getMetaData(rebecDefinition.getType());
		 itsClass = Rebeca2ROSTypesUtilities.getClassName(rebecDefinition.getType(), rebecaModel);
		
		List<Expression> binding = rebecDefinition.getBindings();
		List<FieldDeclaration> knownrebecs = itsClass.getKnownRebecs();
		Map <String, String> knownrebecsBinding = new HashMap<String, String>();

		int i = 0;
		for(FieldDeclaration fd : knownrebecs) {
			for(VariableDeclarator vd : fd.getVariableDeclarators()) {
				knownrebecsBinding.put(vd.getVariableName(), ((TermPrimary)binding.get(i)).getName());
				i++;
			}
		}
			
		 
		 
		 for(MsgsrvDeclaration msgsrv : itsClass.getMsgsrvs()) {
			retValue += "<remap from=" + QUOTE_MARK + 
						 itsClass.getName()  + "/" + msgsrv.getName() + QUOTE_MARK;
			retValue += " to=" + QUOTE_MARK +
					rebecDefinition.getName() + "/" + msgsrv.getName() + QUOTE_MARK;
			retValue += "/>" + NEW_LINE;
		}
		
	
		
		Map<Pair<String, String>, String> methodCalls = new HashMap<Pair<String, String>, String>();
		expressionTransformer.prepare(modelName, itsClass, rebecaModel);
		reactiveClassTransformer.prepare(itsClass);
		methodCalls = reactiveClassTransformer.getMethodCalls();
		
		if(methodCalls.isEmpty())
			return retValue;
		Iterator<Entry<Pair<String, String>, String>> it = methodCalls.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Pair<String, String>, String> entry = 
					(Map.Entry<Pair<String, String>, String>)it.next();
			if(! entry.getKey().getFirst().equals("self")) {
				retValue += "<remap from=";
				retValue += QUOTE_MARK + entry.getKey().getFirst() + "/" + entry.getKey().getSecond() + QUOTE_MARK;
				retValue += " to=" + QUOTE_MARK + knownrebecsBinding.get(entry.getKey().getFirst()) + "/" + entry.getKey().getSecond() + QUOTE_MARK;
				retValue += "/>" + NEW_LINE;
			}
		}
		
		Iterator<Entry<String, String>> it2 = knownrebecsBinding.entrySet().iterator();
		while(it2.hasNext()) {
			Entry<String, String> entry2 = (Map.Entry<String, String>)it2.next();
			retValue += "<param name=" + QUOTE_MARK + entry2.getKey() + QUOTE_MARK 
					+ " type=" + QUOTE_MARK + "str" + QUOTE_MARK
					+ " value=" + QUOTE_MARK + entry2.getValue() + QUOTE_MARK + "/>" + NEW_LINE;
		}
	
		return retValue;
	}


	private String getParamsDeclarations(RebecaModel rebecaModel) {
		String retValue = "";
		/* add environment variables to the launch file */
		for(FieldDeclaration fieldDeclaration : rebecaModel.getRebecaCode().getEnvironmentVariables()){
			for(VariableDeclarator var: fieldDeclaration.getVariableDeclarators()) {
				VariableInitializer variableInitializer = var.getVariableInitializer();/* ??? how to get the value */
				if(variableInitializer instanceof ArrayVariableInitializer) {
					/* to be handled later */
				} else {
					retValue += "<param ";
					retValue += "name=" + QUOTE_MARK + var.getVariableName() + QUOTE_MARK;
					retValue += " type=" + QUOTE_MARK + 
									getLaunchFileFieldsTypes(fieldDeclaration.getType())+ QUOTE_MARK;
					retValue += " value=";
					retValue += QUOTE_MARK + expressionTransformer.translate(((OrdinaryVariableInitializer)variableInitializer).getValue()) + QUOTE_MARK;
					retValue += " />" + NEW_LINE;
				}
			}
		}
		/* as arrays can't be declared in the ROS launch file, all the environment variables
		 * 	have been defined as constant in the header file */
		return retValue;
	}
	
	private String getLaunchFileFieldsTypes(Type type) {
		String typeName = "";
		if(type.equals(CoreRebecaTypeSystem.INT_TYPE))
			typeName = "int";
		else
			if(type.equals(CoreRebecaTypeSystem.DOUBLE_TYPE))
				typeName = "double";
			else
				if(type.equals(CoreRebecaTypeSystem.BOOLEAN_TYPE))
					typeName = "bool";
				else
					typeName = "str"; 
		return typeName;
	}
	
}