package org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartInvariantInstructionBean;

import java.util.ArrayList;

public class InvariantConditionTranslator extends AbstractExpressionTranslator {

    public InvariantConditionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }

    public void setComputedModeName(String computedModeName) {
        this.computedModeName = computedModeName;
    }

    String computedModeName;
    @Override
    public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
        BinaryExpression binaryExpression = (BinaryExpression) expression;
        String operator = binaryExpression.getOperator();
        Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), instructions);
        Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), instructions);

        String stringInvariantExpression = leftSide + operator + rightSide;
        StartInvariantInstructionBean startInvariantInstructionBean = new StartInvariantInstructionBean(computedModeName, stringInvariantExpression);
        instructions.add(startInvariantInstructionBean);
        return instructions;
    }
}
