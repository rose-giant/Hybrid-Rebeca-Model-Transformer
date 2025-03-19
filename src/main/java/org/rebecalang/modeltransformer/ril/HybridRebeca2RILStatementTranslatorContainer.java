//package org.rebecalang.modeltransformer.ril;
//
//import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
//import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
//import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
//import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.PhysicalClassDeclaration;
//import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
//import org.rebecalang.modeltransformer.ril.corerebeca.translator.AbstractStatementTranslator;
//import org.rebecalang.modeltransformer.ril.corerebeca.translator.BlockStatementTranslator;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Hashtable;
//
//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//public class HybridRebeca2RILStatementTranslatorContainer extends Rebeca2RILStatementTranslatorContainer{
//
//    private PhysicalClassDeclaration physicalClassDeclaration;
//    private HybridRebeca2RILExpressionTranslatorContainer expressionTranslatorContainer;
//    public Hashtable<Class<? extends Statement>, AbstractStatementTranslator> translators;
//
//    public HybridRebeca2RILStatementTranslatorContainer() {
//        translators = new Hashtable<Class<? extends Statement>, AbstractStatementTranslator>();
//    }
//
//    public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
//        System.out.println("hybrid statement translators are " + translators.size() + translators  );
//
//        if(statement instanceof Expression)
//            expressionTranslatorContainer.translate((Expression) statement, instructions);
//        else
//            translators.get(statement.getClass()).translate(statement , instructions);
//    }
//
//    public void preparePhysical(PhysicalClassDeclaration pcd, String computedModeName) {
//        this.setPhysicalClassDeclaration(pcd);
//        this.setComputedModeName(computedModeName);
//    }
//
//    public PhysicalClassDeclaration getPhysicalClassDeclaration() {
//        return  physicalClassDeclaration;
//    }
//
//    public void setPhysicalClassDeclaration(PhysicalClassDeclaration physicalClassDeclaration) {
//        this.physicalClassDeclaration = physicalClassDeclaration;
//        this.expressionTranslatorContainer.setPhysicalClassDeclaration(physicalClassDeclaration);
//    }
//
//}
