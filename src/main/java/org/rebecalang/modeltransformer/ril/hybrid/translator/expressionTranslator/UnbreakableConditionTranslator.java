package org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator;

import com.sun.codemodel.JForEach;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.HybridTermPrimary;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.BinaryExpressionTranslator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class UnbreakableConditionTranslator extends AbstractExpressionTranslator {

    BinaryExpressionTranslator binaryExpressionTranslator = new BinaryExpressionTranslator(expressionTranslatorContainer);
    public UnbreakableConditionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }

    public void setComputedModeName(String computedModeName) {
        this.computedModeName = computedModeName;
    }

    Class instructionBean;
    public void setBeanClass(Class<? extends InstructionBean> instructionClass) {
        instructionBean = instructionClass;
    }

    String computedModeName;
    @Override
    public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
        BinaryExpression binaryExpression = (BinaryExpression) expression;
        String operator = binaryExpression.getOperator();
        Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), instructions);
        Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), instructions);

//        System.out.println("we have " + leftSide + operator + rightSide);
        String stringInvariantExpression = leftSide + operator + rightSide;

        try {
            Constructor<? extends InstructionBean> constructor = instructionBean.getConstructor(String.class, String.class);
            InstructionBean instruction = constructor.newInstance(computedModeName, stringInvariantExpression);

            instructions.add(instruction);
            return instructions;
        } catch (Exception e) {
            throw new RuntimeException("Error creating instruction bean: " + instructionBean.getSimpleName(), e);
        }
    }

    ArrayList<Object> operands = new ArrayList<>();
    ArrayList<String> binaryExpressions = new ArrayList<>();
    ArrayList<String> operators = new ArrayList<>();

    private String binaryTranslate(Expression expression) {
        ArrayList<InstructionBean> dummyInstructions = new ArrayList<>();
        BinaryExpression binaryExpression = (BinaryExpression) expression;
        String operator = binaryExpression.getOperator();

//        BinaryExpressionTranslator binaryExpressionTranslator = new BinaryExpressionTranslator(expressionTranslatorContainer);
        binaryExpressionTranslator.setInstructionsToBeAdded(false);

        Object leftSide = new Object();
        Object rightSide = new Object();

        if (binaryExpression.getLeft() instanceof BinaryExpression) {
            leftSide = binaryExpressionTranslator.translate(binaryExpression.getLeft(), dummyInstructions);
            operands.add(leftSide);
        } else {
            expressionTranslatorContainer.translate(binaryExpression.getLeft(), dummyInstructions);
//            operands.add(binaryExpression.getLeft());
        }

        if (binaryExpression.getRight() instanceof BinaryExpression) {
            rightSide = binaryExpressionTranslator.translate(binaryExpression.getRight(), dummyInstructions);
            operands.add(rightSide);
        } else {
            expressionTranslatorContainer.translate(binaryExpression.getRight(), dummyInstructions);
//            operands.add(binaryExpression.getRight());
        }

//        Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), dummyInstructions);
//        Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), dummyInstructions);

        operators.add(operator);
        binaryExpressions.add(leftSide + operator + rightSide);
        String binaryExpressionStr = leftSide.toString() + operator + rightSide.toString();

        return binaryExpressionStr;
    }

}
