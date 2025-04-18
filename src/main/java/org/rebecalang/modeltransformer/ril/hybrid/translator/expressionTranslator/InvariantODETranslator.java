package org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartODEInstructionBean;

import java.util.ArrayList;

public class InvariantODETranslator extends AbstractExpressionTranslator {
    public InvariantODETranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }

    String computedModeName;
    public void setComputedModeName(String computedModeName) {
        this.computedModeName = computedModeName;
    }

    int odeIndex = 0;
    public void setOdeIndex(int odeIndex) {
        this.odeIndex = odeIndex;
    }

    @Override
    public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
        BinaryExpression binaryExpression = (BinaryExpression) expression;
        String operator = binaryExpression.getOperator();
        Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), instructions);
        Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), instructions);

        String stringODEExpression = leftSide+ "'" + operator + rightSide;
        StartODEInstructionBean startODEInstructionBean = new StartODEInstructionBean(computedModeName, stringODEExpression);
        startODEInstructionBean.setOdeIndex(this.odeIndex);
        instructions.add(startODEInstructionBean);
        return instructions;
    }
}
