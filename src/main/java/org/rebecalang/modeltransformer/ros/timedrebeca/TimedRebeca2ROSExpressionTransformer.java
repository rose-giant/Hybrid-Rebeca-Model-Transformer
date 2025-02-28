package org.rebecalang.modeltransformer.ros.timedrebeca;

import java.util.HashMap;
import java.util.Map;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecInstantiationPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TernaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.timedrebeca.TimedRebecaTypeSystem;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.StatementTransformingException;
import org.rebecalang.modeltransformer.ros.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TimedRebeca2ROSExpressionTransformer {
	public final static String NEW_LINE = "\r\n";
	public final static String TAB = "\t";

	static Integer i = 0;
	private String modelName;
	private ReactiveClassDeclaration rc;
	private RebecaModel rebecaModel;
	private Map <Pair<String, String>, String> methodCalls = new HashMap<Pair<String, String>, String>();

	@Autowired
	TimedRebecaTypeSystem timedRebecaTypeSystem;
	@Autowired
	ExceptionContainer exceptionContainer;

	public void prepare(String modelName, ReactiveClassDeclaration rc, RebecaModel rebecaModel) {
		this.modelName = modelName;
		this.rebecaModel = rebecaModel;
		this.rc = rc;
	}

	public String translate(Expression expression) {
		String retValue = "";
		if (expression instanceof TernaryExpression) {
			TernaryExpression tExpression = (TernaryExpression)expression;
			Expression condition = tExpression.getCondition();
			retValue = "(" + (translate(condition)) + ")";
			retValue += " ? " + "(" + translate(tExpression.getLeft()) + ")";
			retValue += " : " + "(" + translate(tExpression.getRight()) + ")";
		} else if (expression instanceof BinaryExpression) {
			BinaryExpression bExpression = (BinaryExpression) expression;
			String op = bExpression.getOperator();
			retValue =  translate(bExpression.getLeft()) +
					 " " + op + " " + translate(bExpression.getRight());
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression uExpression = (UnaryExpression) expression;
			retValue = uExpression.getOperator() + " " + translate(uExpression.getExpression());
		} else if (expression instanceof CastExpression) {
			exceptionContainer.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"cast\" expression.", 
					expression.getLineNumber(), expression.getCharacter()));
		} else if (expression instanceof NonDetExpression) {
			NonDetExpression nonDetExpression = (NonDetExpression)expression;
			int numberOfChoices = nonDetExpression.getChoices().size();
			retValue += nonDetExpression.getType().getTypeName();
			retValue += "int numberOfChoices = " + Integer.toString(numberOfChoices) + ";" + NEW_LINE;
			retValue += "int choice = " + "rand() % " + Integer.toString(numberOfChoices) + ";" + NEW_LINE;
			int index = numberOfChoices;
			for (Expression nonDetChoice : ((NonDetExpression)expression).getChoices()) {
				retValue += "if (" + "choice ==" + Integer.toString(numberOfChoices - index) + ")" + NEW_LINE;
				retValue += ((NonDetExpression)nonDetChoice);
				index ++;
			} 
		} else if (expression instanceof Literal) {
			Literal lExpression = (Literal) expression;
			retValue = lExpression.getLiteralValue();
			if (retValue.equals("null"))
				retValue = "\"dummy\"";
		} else if (expression instanceof PlusSubExpression) {
				retValue = translate(((PlusSubExpression)expression).getValue()) + 
						((PlusSubExpression)expression).getOperator();
		} else if (expression instanceof PrimaryExpression) {
			PrimaryExpression pExpression = (PrimaryExpression) expression;
			retValue = translatePrimaryExpression(pExpression);
		} 
			else {
				exceptionContainer.addException(
						new StatementTransformingException("Unknown translation rule for expression type " 
					+ expression.getClass(), expression.getLineNumber(), expression.getCharacter()));
		}
		return retValue;
	}

	protected String translatePrimaryExpression(PrimaryExpression pExpression) {
		String retValue = "";
		if (pExpression instanceof DotPrimary) {
			DotPrimary dotPrimary = (DotPrimary) pExpression;
			retValue = translateDotPrimary(dotPrimary);
		} else if (pExpression instanceof TermPrimary) {
			retValue = translatePrimaryTermExpression((TermPrimary) pExpression);
		} else if (pExpression instanceof RebecInstantiationPrimary) {
			RebecInstantiationPrimary rip = (RebecInstantiationPrimary) pExpression;
			boolean hasMoreVariable = false;
			String args = "";
			try {
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) timedRebecaTypeSystem.getMetaData(rip.getType());
				if (!rcd.getStatevars().isEmpty()) {
					args += " , ";
					for (FieldDeclaration fd : rcd.getStatevars()) {
						for (VariableDeclarator vd : fd.getVariableDeclarators()) {
							hasMoreVariable = true;
							String typeInit = fd.getType() == CoreRebecaTypeSystem.BOOLEAN_TYPE ? "false" : 
								fd.getType().canTypeCastTo(CoreRebecaTypeSystem.INT_TYPE) ? "0" : "\"dummy\"";
							args += "(" + rcd.getName() + "-" + vd.getVariableName() + " |-> " + typeInit + ") " ;
						}
					}
				}
				if (!hasMoreVariable)
					args += "emptyValuation";
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
			args += ",";
			hasMoreVariable = false;
			String typeName = rip.getType().getTypeName();
			for (Expression expression : rip.getBindings()) {
				args += " arg(" + translate(expression) + ")";
				hasMoreVariable = true;
			}
			for (Expression expression : rip.getArguments()) {
				args += " arg(" + translate(expression) + ")";
				hasMoreVariable = true;
			}
			if (!hasMoreVariable)
				args += "noArg";
			retValue = " new (" + typeName + args + ")";
		} else {
			exceptionContainer.addException(new StatementTransformingException("Unknown translation rule for initializer type " 
					+ pExpression.getClass(), pExpression.getLineNumber(), pExpression.getCharacter()));
		}
		return retValue;
	}

	private String translateDotPrimary(DotPrimary dotPrimary) {
		String retValue = "";
		if (!(dotPrimary.getLeft() instanceof TermPrimary) || !(dotPrimary.getRight() instanceof TermPrimary)) {
			exceptionContainer.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"nested record access expression.", 
					dotPrimary.getLineNumber(), dotPrimary.getCharacter()));
		} else {
//			TODO: Modified by Ehsan as the return vlaue type of message servers is always set to MSGSRV_TYPE
//			if(TypesUtilities.getInstance().getSuperType(dotPrimary.getRight().getType()) == TypesUtilities.MSGSRV_TYPE) {
			if(dotPrimary.getRight().getType() == CoreRebecaTypeSystem.MSGSRV_TYPE) {
				retValue = mapToROSPublishing(dotPrimary);
			} 
		}
		return retValue;
	}


	private String mapToROSPublishing(DotPrimary dotPrimary) {
		String retValue = "";
		/* map to ROS Publishing */
		retValue = modelName + "::" + ((TermPrimary)dotPrimary.getRight()).getName() 
				+ " " + "pubMsg" + i.toString() + ";" + NEW_LINE;
		
		/* fill the ROS message fields with the arguments to be published */
		int argumentIndex = 0;
		for (Expression expression : ((TermPrimary)dotPrimary.getRight()).getParentSuffixPrimary().getArguments()) {
				ReactiveClassDeclaration toClass = null;
				TermPrimary toRebec = (TermPrimary)dotPrimary.getLeft();
				toClass = Utilities.findKnownReactiveClass(rc, toRebec.getName(), rebecaModel);
				String toMsgsrvName = ((TermPrimary)dotPrimary.getRight()).getName();
				MsgsrvDeclaration toMsgsrv = Utilities.findTheMsgsrv(toClass, toMsgsrvName);
				String argumentName = toMsgsrv.getFormalParameters().get(argumentIndex).getName();
				retValue += "pubMsg" + i.toString() + "." + argumentName + " = " + translate(expression) + ";" + NEW_LINE;
				argumentIndex ++;
				
		} 
	
		retValue += "pubMsg" + i.toString() + "." + "sender" + "=" + "sender" + ";" + NEW_LINE;			
		retValue += ((TermPrimary) dotPrimary.getLeft()).getName() + "_" + ((TermPrimary)dotPrimary.getRight()).getName() + "_pub"
				+ "." + "publish(" + "pubMsg" + i.toString() + ")" + ";" + NEW_LINE;
		
		i ++; /* to prevent from repeated names */
		/* end of publishing */
		
		/* storing the name of callee rebec and the name of called msgsrv in order to declare publishers */
		Pair<String, String> methodCall = new Pair<String, String>(
				((TermPrimary)dotPrimary.getLeft()).getName(), ((TermPrimary)dotPrimary.getRight()).getName() );
		methodCalls.put(methodCall, "");

		//ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) TransformingContext.getInstance().lookupInContext("current-reactive-class");
		//retValue = ((TermPrimary) dotPrimary.getLeft()).getName();
		//String typeName = TypesUtilities.getTypeName(((TermPrimary) dotPrimary.getLeft()).getType());
		//System.out.println(typeName);
		return retValue;
	}

	private String translatePrimaryTermExpression(TermPrimary pExpression) {
		String retValue = "";
		if(pExpression.getName().equals("assertion") || 
				pExpression.getName().equals("after") ||
				pExpression.getName().equals("deadline")){
			return retValue;
		}
		
		if(pExpression.getName().equals("delay"))
			retValue += "sleep";
		else if(pExpression.getName().equals("sender"))
			return "thisMsg.sender";
		else
			retValue += pExpression.getName();
		
		if( pExpression.getParentSuffixPrimary() != null) {
			retValue += "(";
			for(Expression argument: pExpression.getParentSuffixPrimary().getArguments()) {
				retValue += translate(argument) + ",";
			}
			if(! pExpression.getParentSuffixPrimary().getArguments().isEmpty()) {
				retValue = retValue.substring(0, retValue.length() - 1);
			}
			retValue += ")";
		}
		//To support movement in ROS
		if (retValue.compareTo("Move(1,0)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if (retValue.compareTo("Move(0,1)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if (retValue.compareTo("Move(-1,0)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if (retValue.compareTo("Move(0,-1)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if(retValue.compareTo("Move(1,1)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if(retValue.compareTo("Move(1,-1)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if(retValue.compareTo("Move(-1,1)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		else if(retValue.compareTo("Move(-1,-1)")==0)
		{
			//ROSCode to publish on CM_Vel topic
		}
		
		
		/* to support arrays */
		for(Expression ex: pExpression.getIndices()) {
			retValue += "[" + translate(ex) + "]";
		}

		return retValue;
	}

	public Map <Pair<String, String>, String> getMethodCalls() {
		return methodCalls;
	}
	
}