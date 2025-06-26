package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.SymbolTableException;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Label;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartSetModeInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component("CORE_REBECA_TERM_PRIMARY")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TermPrimaryExpressionTranslator extends AbstractExpressionTranslator {

	@Autowired
	public TermPrimaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Autowired
	protected GenericApplicationContext appContext;

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		ReactiveClassDeclaration reactiveClassDeclaration = expressionTranslatorContainer.getReactiveClassDeclaration();
		Type baseType = null;
		if(reactiveClassDeclaration != null) {
			try {
				AbstractTypeSystem typeSystem = (AbstractTypeSystem) appContext.getBean("typeSystem");
				baseType = typeSystem.getType(reactiveClassDeclaration.getName());
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
		}
		Variable base = null;
		if (!isBuiltInMethod(expression))
			base = new Variable("self");

		return translate(baseType, base, (TermPrimary) expression, instructions);
	}

	protected boolean isBuiltInMethod(Expression expression) {
		return isPOWMethod((TermPrimary) expression) || isSQRTMethod((TermPrimary) expression)
				|| isAssertionMethod((TermPrimary) expression) || isGetAllActorsMethod((TermPrimary) expression)
				|| isDelayMethod((TermPrimary) expression);
	}

	public Object translate(Type baseType, Variable baseVariable, TermPrimary termPrimary,
			ArrayList<InstructionBean> instructions) {

		addDelayMethodToSymbolTable(baseType, termPrimary);
		addSetModeToSymbolTable(baseType, termPrimary);

		if (termPrimary.getParentSuffixPrimary() == null)
			return (new Variable(termPrimary.getName()));

		List<Expression> arguments = termPrimary.getParentSuffixPrimary().getArguments();
		ArrayList<Object> argumentsValues = new ArrayList<>();
		ArrayList<Type> argumentsType = new ArrayList<>();

		for (Expression argument : arguments) {
			argumentsValues.add(expressionTranslatorContainer.translate(
					argument, instructions));
			argumentsType.add(argument.getType());
		}
		SymbolTable symbolTable = expressionTranslatorContainer.getSymbolTable();

		MethodInSymbolTableSpecifier castableMethodSpecification;

		castableMethodSpecification = getMethodFromSymbolTable(baseType, termPrimary,
				argumentsType, symbolTable);

		String computedMethodName;
		if (castableMethodSpecification == null)
			assert false;//return null;

		if (isBuiltInMethod(termPrimary))
			computedMethodName = RILUtilities.computeMethodName(castableMethodSpecification);
		else {
			computedMethodName = RILUtilities.computeMethodName(castableMethodSpecification.getRebecType(), castableMethodSpecification);
		}

		Map<String, Object> passedParameters = new TreeMap<>();
		List<String> argumentsNames = castableMethodSpecification.getArgumentsNames();
		for(int cnt = 0; cnt < argumentsNames.size(); cnt++)
			passedParameters.put(argumentsNames.get(cnt), argumentsValues.get(cnt));

		if (termPrimary.getType() == CoreRebecaTypeSystem.MSGSRV_TYPE) {
			instructions.add(createMsgSrvCallInstructionBean(baseVariable, passedParameters, computedMethodName, termPrimary, instructions));
			return null;
		}

		if (computedMethodName.startsWith("delay")) {
			if (!isNull(passedParameters.get(INTERVAL_UP_KEY)) && !isNull(passedParameters.get(INTERVAL_LOW_KEY)))
				translateContinuousDelayArgs(passedParameters, instructions);
			else if (!isNull(passedParameters.get(INTERVAL_LOW_KEY))) translateDelayArgs(passedParameters, instructions);

			instructions.add(new MethodCallInstructionBean(baseVariable, termPrimary.getName(), passedParameters, null));
			return null;
		}

		if (computedMethodName.contains("setMode")) {
			assert (!isNull(passedParameters.get(MODE_NAME)));
			instructions.add(new StartSetModeInstructionBean( passedParameters.get(MODE_NAME).toString() ));
			return null;
		}

		Variable tempVariable = null;
		if (termPrimary.getType() != CoreRebecaTypeSystem.VOID_TYPE) {
			tempVariable = AbstractExpressionTranslator.getTempVariable();
			instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		}

		if (termPrimary.getLabel() == CoreRebecaLabelUtility.BUILT_IN_METHOD) {
			instructions.add(new ExternalMethodCallInstructionBean(baseVariable, computedMethodName,
					passedParameters, tempVariable));
		} else {
			MethodCallInstructionBean methodCallInstructionBean = new MethodCallInstructionBean(baseVariable,
					computedMethodName);
			methodCallInstructionBean.setFunctionCallResult(tempVariable);
			instructions.add(methodCallInstructionBean);
		}

		return tempVariable;
	}

	public static final String INTERVAL_LOW_KEY = "bound";
	public static final String INTERVAL_UP_KEY = "intervalUp";
	public static final String MODE_NAME = "modeName";

	public static void translateContinuousDelayArgs(Map<String, Object> passedParameters, ArrayList<InstructionBean> instructions) {
		Object lowerBound = passedParameters.get(INTERVAL_LOW_KEY);
		Object upperBound = passedParameters.get(INTERVAL_UP_KEY);
		if (lowerBound instanceof Expression)
			lowerBound = expressionTranslatorContainer.translate((Expression) lowerBound, instructions);

		if (upperBound instanceof Expression)
			upperBound = expressionTranslatorContainer.translate((Expression) upperBound, instructions);

		Variable tempVariable = getTempVariable();
		instructions.add(new ContnuousNonDetInstructionBean(tempVariable, lowerBound.toString(), upperBound.toString()));
		passedParameters.put(INTERVAL_LOW_KEY, tempVariable);
		passedParameters.remove(INTERVAL_UP_KEY);
	}

	public static void translateDelayArgs(Map<String, Object> passedParameters, ArrayList<InstructionBean> instructions) {
		Object result;
		Object argLow = passedParameters.get(INTERVAL_LOW_KEY);
		if (argLow instanceof NonDetValue) {
			Variable tempVariable = getTempVariable();
			AssignmentInstructionBean assignmentInstruction = new AssignmentInstructionBean(tempVariable,
					argLow.toString(), null, null);
			instructions.add(assignmentInstruction);
			result = tempVariable;
		}
		else if (argLow instanceof Variable) {
			result = argLow;
		}
		else if (!(argLow instanceof Literal || argLow instanceof UnaryExpression || argLow instanceof Float ||
				argLow instanceof Integer)) {
			result = expressionTranslatorContainer.translate((Expression) argLow, instructions);
		} else {
			result = passedParameters.get(INTERVAL_LOW_KEY);
		}

		passedParameters.put(INTERVAL_LOW_KEY, result);
	}

	private void addDelayMethodToSymbolTable(Type baseType, TermPrimary termPrimary) {
		Type container = baseType;
		MethodDeclaration methodDeclaration = new MethodDeclaration();
		methodDeclaration.setName("delay");
		Label label = new Label();
		label.setName(String.valueOf(CoreRebecaLabelUtility.BUILT_IN_METHOD));
		methodDeclaration.setLineNumber(termPrimary.getLineNumber());
		methodDeclaration.setCharacter(termPrimary.getCharacter());
		expressionTranslatorContainer.addMethodToSymbolTable(container, methodDeclaration, label);
	}

	private void addSetModeToSymbolTable(Type baseType, TermPrimary termPrimary) {
		Type container = baseType;
		MethodDeclaration methodDeclaration = new MethodDeclaration();
		methodDeclaration.setName("setMode");
		Label label = new Label();
		label.setName(String.valueOf(CoreRebecaLabelUtility.BUILT_IN_METHOD));
		methodDeclaration.setLineNumber(termPrimary.getLineNumber());
		methodDeclaration.setCharacter(termPrimary.getCharacter());
		expressionTranslatorContainer.addMethodToSymbolTable(container, methodDeclaration, label);
	}

	protected MsgsrvCallInstructionBean createMsgSrvCallInstructionBean(Variable baseVariable,
			Map<String, Object> parameters, String computedMethodName, TermPrimary termPrimary,
			ArrayList<InstructionBean> instructions) {
		return new MsgsrvCallInstructionBean(baseVariable, computedMethodName, parameters);
	}

	private MethodInSymbolTableSpecifier getMethodFromSymbolTable(Type baseType, TermPrimary termPrimary,
			ArrayList<Type> argumentsType, SymbolTable symbolTable) {

		MethodInSymbolTableSpecifier methodInSymbolTableSpecifier;
		Type curType = baseType;

		if(termPrimary.getName().equals("delay")) {
			List<String> argNames = new ArrayList<>();
			argNames.add(INTERVAL_LOW_KEY);
			if (termPrimary.getParentSuffixPrimary().getArguments().size() == 2) {
				argNames.add(INTERVAL_UP_KEY);
			}

			methodInSymbolTableSpecifier = symbolTable.new MethodInSymbolTableSpecifier(termPrimary.getName(),
					termPrimary.getLabel(), curType, argumentsType, argNames);

			return methodInSymbolTableSpecifier;
		}

		if(termPrimary.getName().equals("setMode")) {
			List<String> argNames = new ArrayList<>();

			argNames.add(MODE_NAME);

			methodInSymbolTableSpecifier = symbolTable.new MethodInSymbolTableSpecifier(termPrimary.getName(),
					termPrimary.getLabel(), curType, argumentsType, argNames);
			methodInSymbolTableSpecifier.setRebecType(curType);

			return methodInSymbolTableSpecifier;
		}

		while (true) {
			try {
				methodInSymbolTableSpecifier = symbolTable.getCastableMethodSpecification(curType,
						termPrimary.getName(), argumentsType);
				return methodInSymbolTableSpecifier;
			} catch (SymbolTableException ste) {
				try {
					ReactiveClassDeclaration metaData = (ReactiveClassDeclaration) curType.getTypeSystem()
							.getMetaData(curType);
					if (metaData.getExtends() == null) {
						try {
							return symbolTable.getCastableMethodSpecification(CoreRebecaTypeSystem.NO_TYPE,
									termPrimary.getName(), argumentsType);
						} catch (SymbolTableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							return null;
						}
					} else {
						curType = metaData.getExtends();
					}
				} catch (CodeCompilationException e) {
					e.printStackTrace();
				}
			}
		}
    }

	private boolean isDelayMethod(TermPrimary statement) {
		boolean returnValue = statement.getName().equals("delay");
        return returnValue;
	}

	private boolean isAssertionMethod(TermPrimary statement) {
		if (!statement.getName().equals("assertion"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		int size = statement.getParentSuffixPrimary().getArguments().size();
		if (size != 1 && size != 2)
			return false;
		if (!statement.getParentSuffixPrimary().getArguments().get(0).getType()
				.canTypeCastTo(CoreRebecaTypeSystem.BOOLEAN_TYPE))
			return false;
		if (size == 2)
            return statement.getParentSuffixPrimary().getArguments().get(1).getType()
                    .canTypeCastTo(CoreRebecaTypeSystem.STRING_TYPE);
		return true;
	}

	private boolean isGetAllActorsMethod(TermPrimary statement) {
		if (!statement.getName().equals("getAllActors"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
        return statement.getParentSuffixPrimary().getArguments().size() == 0;
    }

	private boolean isPOWMethod(TermPrimary statement) {
		if (!statement.getName().equals("pow"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		if (statement.getParentSuffixPrimary().getArguments().size() != 2)
			return false;
		if (!statement.getParentSuffixPrimary().getArguments().get(0).getType()
				.canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE))
			return false;
        return statement.getParentSuffixPrimary().getArguments().get(1).getType()
                .canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE);
    }

	private boolean isSQRTMethod(TermPrimary statement) {
		if (!statement.getName().equals("sqrt"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		if (statement.getParentSuffixPrimary().getArguments().size() != 1)
			return false;
        return statement.getParentSuffixPrimary().getArguments().get(0).getType()
                .canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE);
    }

}
