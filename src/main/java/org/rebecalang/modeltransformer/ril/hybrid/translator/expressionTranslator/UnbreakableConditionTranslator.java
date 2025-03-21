package org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class UnbreakableConditionTranslator extends AbstractExpressionTranslator {

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

        System.out.println("we have " + leftSide + operator + rightSide);
        String stringInvariantExpression = leftSide + operator + rightSide;

        try {
            Constructor<? extends InstructionBean> constructor = instructionBean.getConstructor(String.class, String.class);
            System.out.println("the caught class is " + constructor.getClass());
            InstructionBean instruction = constructor.newInstance(computedModeName, stringInvariantExpression);

            instructions.add(instruction);
            return instructions;
        } catch (Exception e) {
            throw new RuntimeException("Error creating instruction bean: " + instructionBean.getSimpleName(), e);
        }
    }

}
