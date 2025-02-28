package org.rebecalang.modeltransformer.ril.hybrid.translator;

import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.TermPrimaryExpressionTranslator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HybridRebecaPrimaryExpressionTranslator extends TermPrimaryExpressionTranslator {

    public HybridRebecaPrimaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }
}
