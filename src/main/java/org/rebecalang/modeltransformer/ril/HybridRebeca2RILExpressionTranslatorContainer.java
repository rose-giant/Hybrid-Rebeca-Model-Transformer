//package org.rebecalang.modeltransformer.ril;
//
//import org.rebecalang.compiler.modelcompiler.SymbolTable;
//import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
//import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.PhysicalClassDeclaration;
//import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
//import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Hashtable;
//
//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//public class HybridRebeca2RILExpressionTranslatorContainer extends Rebeca2RILExpressionTranslatorContainer{
//
//    Hashtable<Class<? extends Expression>, AbstractExpressionTranslator> translators;
//    private PhysicalClassDeclaration physicalClassDeclaration;
//    private SymbolTable symbolTable;
//
//    public HybridRebeca2RILExpressionTranslatorContainer() {
//        translators = new Hashtable<Class<? extends Expression>, AbstractExpressionTranslator>();
//    }
//
//    public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
//        System.out.println("hybrid expression translators are " + translators.size() + translators  );
//
//        AbstractExpressionTranslator expressionTranslator = translators.get(expression.getClass());
//        Object expressionResult = expressionTranslator.translate(expression, instructions);
//        return expressionResult;
//
//    }
//
//    public PhysicalClassDeclaration getPhysicalClassDeclaration() {
//        return physicalClassDeclaration;
//    }
//
//    public void setPhysicalClassDeclaration(PhysicalClassDeclaration physicalClassDeclaration) {
//        this.physicalClassDeclaration = physicalClassDeclaration;
//    }
//}
