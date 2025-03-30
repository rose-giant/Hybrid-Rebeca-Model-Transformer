package org.rebecalang.modeltransformer;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class HybridRebecaToRILTest {
    @Autowired
    Rebeca2RILModelTransformer rebeca2RIL;

    @Autowired
    RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    public ExceptionContainer exceptionContainer;

    private String HYBRID_MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modeltransformer/testcase/Hybrid/";

    protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    @Test
    public void PhysicalRebecIsSuccessfullyRecognized() {
        String modelName = "classicHybrid";
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }

    //does not handle t < 2 + 4 or t - 1 > 3
    @Test
    public void InvariantConditionIsTransformedToRils() {
        // Model Name is replaced with the minimal Rebeca code you mentioned: main{}
        String modelName = "main";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }

    @Test
    public void generatePhysicalModeRIL() {
        // Model Name is replaced with the minimal Rebeca code you mentioned: main{}
        String modelName = "hybridMode";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }
    
    @Test
    public void transformHeaterWithSensorRebecaToRIL() {
        // Model Name is replaced with the minimal Rebeca code you mentioned: main{}
        String modelName = "heaterWithOnePhysicalClass";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }

    @Test
    public void transformRebecaToRIL() {
        // Model Name is replaced with the minimal Rebeca code you mentioned: main{}
        String modelName = "main";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }

    @Test
    public void DelayStmtIsSuccessfullyRecognized() {
        String modelName = "delayContainerModel";
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }
}
