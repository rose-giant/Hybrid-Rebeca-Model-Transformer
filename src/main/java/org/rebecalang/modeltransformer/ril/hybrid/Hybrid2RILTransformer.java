package org.rebecalang.modeltransformer.ril.hybrid;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.hybridrebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.*;
import org.rebecalang.modeltransformer.ril.corerebeca.CoreRebecaModel2RILTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.*;
import org.rebecalang.modeltransformer.ril.hybrid.translator.HybridRebecaTermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator.UnbreakableConditionTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator.InvariantBlockTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator.InvariantODETranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Component
@Qualifier("HYBRID_REBECA")
public class Hybrid2RILTransformer extends CoreRebecaModel2RILTransformer {

    private Rebeca2RILStatementTranslatorContainer statementTranslatorContainer;
    private Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer;

    @Autowired
    public Hybrid2RILTransformer(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
                                 Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {

        super(statementTranslatorContainer, expressionTranslatorContainer);
        this.statementTranslatorContainer = statementTranslatorContainer;
        this.expressionTranslatorContainer = expressionTranslatorContainer;
    }

    @Override
    public void initializeTranslators() {
        super.initializeTranslators();
        expressionTranslatorContainer.registerTranslator(HybridTermPrimary.class,
                appContext.getBean(HybridRebecaTermPrimaryExpressionTranslator.class, expressionTranslatorContainer));
    }

    @Override
    public RILModel transformModel(
            Pair<RebecaModel, SymbolTable> model,
            Set<CompilerExtension> extension,
            CoreVersion coreVersion) {
        RILModel transformedRILModel = new RILModel();

        RebecaModel rebecaModel = model.getFirst();

        statementTranslatorContainer.setSymbolTable(model.getSecond());
        expressionTranslatorContainer.setSymbolTable(model.getSecond());

        List<FieldDeclaration> environmentVariables = rebecaModel.getRebecaCode().getEnvironmentVariables();
        for (FieldDeclaration fieldDeclaration: environmentVariables) {
            ArrayList<InstructionBean> instructions = new ArrayList<>();
            instructions.addAll(getEnvDeclarationRIL(fieldDeclaration));
            transformedRILModel.addEnvVariable("envVarDec$" + environmentVariables.indexOf(fieldDeclaration), instructions);
        }

        HybridRebecaCode hybridRebecaCode = (HybridRebecaCode) rebecaModel.getRebecaCode();
        List <PhysicalClassDeclaration> physicalClassDeclarations = hybridRebecaCode.getPhysicalClassDeclaration();
        for (PhysicalClassDeclaration pcd : physicalClassDeclarations) {
            for(MsgsrvDeclaration msgsrv: pcd.getMsgsrvs()) {
                if(msgsrv.isAbstract())
                    continue;
                String computedMethodName = RILUtilities.computeMethodName(pcd, msgsrv);
                ArrayList<InstructionBean> instructions = generateMethodRIL(pcd, computedMethodName, msgsrv.getBlock());
                instructions.add(new EndMsgSrvInstructionBean());
                transformedRILModel.addMethod(computedMethodName, instructions);
            }
            for(ConstructorDeclaration constructorDeclaration : pcd.getConstructors()) {
                String computedMethodName = RILUtilities.computeMethodName(pcd, constructorDeclaration);
                ArrayList<InstructionBean> instructions = generateMethodRIL(pcd, computedMethodName, constructorDeclaration.getBlock());
                instructions.add(new EndMethodInstructionBean());
                transformedRILModel.addMethod(computedMethodName, instructions);
            }
            for(MethodDeclaration methodDeclaration : pcd.getSynchMethods()) {
                if(methodDeclaration.isAbstract())
                    continue;
                String computedMethodName = RILUtilities.computeMethodName(pcd, methodDeclaration);
                ArrayList<InstructionBean> instructions = generateMethodRIL(pcd, computedMethodName, methodDeclaration.getBlock());
                instructions.add(new EndMethodInstructionBean());
                transformedRILModel.addMethod(computedMethodName, instructions);
            }

            //added by rose
            for(ModeDeclaration modeDeclaration : pcd.getModeDeclarations()) {
                String computedModeName = RILUtilities.computeModeName(pcd, modeDeclaration);
                ArrayList<InstructionBean> instructions = generateModeRIL(pcd, computedModeName, modeDeclaration);
                instructions.add(new EndModeInstructionBean());
                transformedRILModel.addMode(computedModeName, instructions);
            }
        }

        List<ReactiveClassDeclaration> reactiveClassDeclarations = rebecaModel.getRebecaCode().getReactiveClassDeclaration();
        for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
            for(MsgsrvDeclaration msgsrv: rcd.getMsgsrvs()) {
                if(msgsrv.isAbstract())
                    continue;
                String computedMethodName = RILUtilities.computeMethodName(rcd, msgsrv);
                ArrayList<InstructionBean> instructions = generateMethodRIL(rcd, computedMethodName, msgsrv.getBlock());
                instructions.add(new EndMsgSrvInstructionBean());
                transformedRILModel.addMethod(computedMethodName, instructions);
            }
            for(ConstructorDeclaration constructorDeclaration : rcd.getConstructors()) {
                String computedMethodName = RILUtilities.computeMethodName(rcd, constructorDeclaration);
                ArrayList<InstructionBean> instructions = generateMethodRIL(rcd, computedMethodName, constructorDeclaration.getBlock());
                instructions.add(new EndMethodInstructionBean());
                transformedRILModel.addMethod(computedMethodName, instructions);
            }
            for(MethodDeclaration methodDeclaration : rcd.getSynchMethods()) {
                if(methodDeclaration.isAbstract())
                    continue;
                String computedMethodName = RILUtilities.computeMethodName(rcd, methodDeclaration);
                ArrayList<InstructionBean> instructions = generateMethodRIL(rcd, computedMethodName, methodDeclaration.getBlock());
                instructions.add(new EndMethodInstructionBean());
                transformedRILModel.addMethod(computedMethodName, instructions);
            }
        }

        BlockStatement blockStatement = new BlockStatement();
        for(MainRebecDefinition mrd : rebecaModel.getRebecaCode().getMainDeclaration().getMainRebecDefinition()) {
            RebecInstantiationPrimary rip = new RebecInstantiationPrimary();
            rip.setCharacter(mrd.getCharacter());
            rip.setLineNumber(mrd.getLineNumber());
            rip.setType(mrd.getType());
            rip.getAnnotations().addAll(mrd.getAnnotations());
            rip.getArguments().addAll(mrd.getArguments());
            rip.getBindings().addAll(mrd.getBindings());
            blockStatement.getStatements().add(rip);
        }
        ArrayList<InstructionBean> instructions = generateMethodRIL(null, "main", blockStatement);
        transformedRILModel.addMethod("main", instructions);

        return transformedRILModel;
    }

    protected ArrayList<InstructionBean> generateModeRIL(PhysicalClassDeclaration pcd, String computedModeName, ModeDeclaration modeDeclaration) {
        ArrayList<InstructionBean> instructions;
        this.statementTranslatorContainer.preparePhysical(pcd, computedModeName);

        InvariantDeclaration invariantDeclaration = modeDeclaration.getInvariantDeclaration();
        instructions = generateInvariantRIL(invariantDeclaration, computedModeName);

        instructions.add(new StartGuardBlockInstructionBean(computedModeName));
        GuardDeclaration guardDeclaration = modeDeclaration.getGuardDeclaration();
        instructions.addAll(generateGuardRIL(guardDeclaration, computedModeName));
        instructions.add(new EndGuardInstructionBean());

        return instructions;
    }

    protected ArrayList<InstructionBean> generateInvariantRIL(InvariantDeclaration invariantDeclaration, String computedModeName) {
        ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();

        Expression invariantCondition = invariantDeclaration.getCondition();
        UnbreakableConditionTranslator invariantConditionTranslator = new UnbreakableConditionTranslator(expressionTranslatorContainer);
        invariantConditionTranslator.setComputedModeName(computedModeName);
        invariantConditionTranslator.setBeanClass(StartInvariantInstructionBean.class);
        instructions = (ArrayList<InstructionBean>) invariantConditionTranslator.translate(invariantCondition, instructions);

        Statement invariantODEBlock = invariantDeclaration.getBlock();
        InvariantBlockTranslator invariantBlockTranslator = new InvariantBlockTranslator(statementTranslatorContainer, expressionTranslatorContainer);
        invariantBlockTranslator.setComputedModeName(computedModeName);
        invariantBlockTranslator.translate(invariantODEBlock, instructions);
        BinaryExpression odeExpression = invariantBlockTranslator.getOdeBinaryExpression();

        InvariantODETranslator invariantODETranslator = new InvariantODETranslator(expressionTranslatorContainer);
        invariantODETranslator.setComputedModeName(computedModeName);
        instructions = (ArrayList<InstructionBean>) invariantODETranslator.translate(odeExpression, instructions);

        return instructions;
    }

    protected ArrayList<InstructionBean> generateGuardRIL(GuardDeclaration guardDeclaration, String computedModeName) {
        ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();

        Expression guardCondition = guardDeclaration.getCondition();
        UnbreakableConditionTranslator guardConditionTranslator = new UnbreakableConditionTranslator(expressionTranslatorContainer);
        guardConditionTranslator.setComputedModeName(computedModeName);
        guardConditionTranslator.setBeanClass(GuardConditionInstructionBean.class);
        instructions = (ArrayList<InstructionBean>) guardConditionTranslator.translate(guardCondition, instructions);

        BlockStatement guardBlock = guardDeclaration.getBlock();
        statementTranslatorContainer.translate(guardBlock, instructions);

        return instructions;
    }
}
