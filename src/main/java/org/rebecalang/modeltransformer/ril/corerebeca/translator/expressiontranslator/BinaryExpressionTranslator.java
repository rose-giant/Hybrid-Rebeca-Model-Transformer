package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
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

	public String leftType = "";
	public String rightType = "";

	@Override
	public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
		BinaryExpression binaryExpression = (BinaryExpression) expression;
		String operator = binaryExpression.getOperator();
		operators.add(operator);
		System.out.println("binaryExpression" + operator);

		Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), instructions);
		operands.add(leftSide);

		Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), instructions);
		operands.add(rightSide);

		setOperandTypes(binaryExpression);

		if (leftType == "float" || rightType == "float" || leftType == "double" || rightType == "double") {
			if (operator.equals("=")) {
				instructions.add(new StartUnbreakableConditionInstructionBean(leftSide, rightSide, operator));
			} else {
				return new StartUnbreakableConditionInstructionBean(leftSide, rightSide, operator);
			}
		} else {
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

	public void setOperandTypes(BinaryExpression binaryExpression) {
		if (binaryExpression.getLeft() instanceof TermPrimary) {
			leftType = String.valueOf(binaryExpression.getLeft().getType().getTypeName());
		} else if (binaryExpression.getLeft() instanceof Literal) {
			leftType = String.valueOf(binaryExpression.getLeft().getType().getTypeName());
		} else if (binaryExpression.getLeft() instanceof BinaryExpression) {
			leftType = "double";
		}

		if (binaryExpression.getRight() instanceof TermPrimary) {
			rightType = String.valueOf(binaryExpression.getRight().getType().getTypeName());
		} else if (binaryExpression.getRight() instanceof Literal) {
			rightType = String.valueOf(binaryExpression.getRight().getType().getTypeName());
		} else if (binaryExpression.getRight() instanceof BinaryExpression) {
			rightType = "double";
		}
	}

}
