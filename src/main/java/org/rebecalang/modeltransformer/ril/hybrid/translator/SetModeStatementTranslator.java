package org.rebecalang.modeltransformer.ril.hybrid.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.AbstractStatementTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SetModeStatementTranslator extends AbstractStatementTranslator {

    @Autowired
    public SetModeStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
                                      Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(statementTranslatorContainer, expressionTranslatorContainer);
    }

    @Override
    public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

    }
}