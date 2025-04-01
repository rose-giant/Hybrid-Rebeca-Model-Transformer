package org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;

import java.util.ArrayList;

public class ContinuousNonDetExpressionTranslator extends AbstractExpressionTranslator {
    public ContinuousNonDetExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }

    @Override
    public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
        return null;
    }
}
