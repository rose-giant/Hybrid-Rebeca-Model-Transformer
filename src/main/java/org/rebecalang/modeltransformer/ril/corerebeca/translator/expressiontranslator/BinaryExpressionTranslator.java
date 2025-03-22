package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BinaryExpressionTranslator extends AbstractExpressionTranslator {

	@Autowired
	public BinaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	boolean instructionsToBeAdded = true;

	public void setInstructionsToBeAdded(boolean instructionsToBeAdded) {
		this.instructionsToBeAdded = instructionsToBeAdded;
	}

	public ArrayList<Object> getOperands() {
		return operands;
	}

	public ArrayList<String> getOperators() {
		return operators;
	}

	ArrayList<Object> operands = new ArrayList<>();
	ArrayList<String> operators = new ArrayList<>();

	@Override
	public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
		BinaryExpression binaryExpression = (BinaryExpression) expression;
		String operator = binaryExpression.getOperator();
		operators.add(operator);

		Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), instructions);
		operands.add(leftSide);

		Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), instructions);
		operands.add(rightSide);

		System.out.println("binary exp here: " + leftSide + operator + rightSide);

		if (instructionsToBeAdded) {
			if (!operator.equals("==") && !operator.equals("!=") && operator.endsWith("=")) {
				AssignmentInstructionBean assignmentInstruction;
				if (operator.equals("=")) {
					assignmentInstruction = new AssignmentInstructionBean(leftSide, rightSide, null, null);
				} else {
					assignmentInstruction = new AssignmentInstructionBean(leftSide,
							leftSide, rightSide, String.valueOf(operator.charAt(0)));
				}
				instructions.add(assignmentInstruction);
			} else {
				Variable tempVariable = getTempVariable();
				instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
				AssignmentInstructionBean assignmentInstruction = new AssignmentInstructionBean(tempVariable,
						leftSide, rightSide, operator);
				instructions.add(assignmentInstruction);
				return tempVariable;
			}
		}
		return null;
	}

}
