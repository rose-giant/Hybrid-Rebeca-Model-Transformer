package org.rebecalang.modeltransformer.ril.hybrid.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.TermPrimaryExpressionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HybridRebecaTermPrimaryExpressionTranslator extends TermPrimaryExpressionTranslator {

    @Autowired
    public HybridRebecaTermPrimaryExpressionTranslator(
            Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }

    @Override
    public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
//		super.coreRebecaTypeSystem = timedRebecaTypeSystem;
//		ReactiveClassDeclaration reactiveClassDeclaration = expressionTranslatorContainer
//				.getReactiveClassDeclaration();
//		Type baseType = null;
//		try {
//			baseType = timedRebecaTypeSystem.getType(reactiveClassDeclaration.getName());
//		} catch (CodeCompilationException e) {
//			e.printStackTrace();
//		}
//		Variable base = null;
//		if (!isBuiltInMethod(expression))
//			base = new Variable("self");

        return super.translate(expression, instructions);
    }


}
