package org.rebecalang.modeltransformer;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.hybridrebeca.HybridRebecaTypeSystem;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.BinaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.Hybrid2RILTransformer;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinaryExpressionTranslatorTest {
    private BinaryExpressionTranslator translator;
    private Rebeca2RILExpressionTranslatorContainer container;
    private Rebeca2RILStatementTranslatorContainer statementTranslatorContainer;
    private Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer;

    @BeforeEach
    void setUp() {
//        Hybrid2RILTransformer hybrid2RILTransformer = new Hybrid2RILTransformer(statementTranslatorContainer,
//                expressionTranslatorContainer);
        container = new Rebeca2RILExpressionTranslatorContainer();
        translator = new BinaryExpressionTranslator(container);
    }

    Rebeca2RILExpressionTranslatorContainer rebeca2RILExpressionTranslatorContainer =
            new Rebeca2RILExpressionTranslatorContainer();

//    @Test
//    void testTranslate_SimpleAssignment() {
//        ArrayList<InstructionBean> instructions = new ArrayList<>();
//
//        Literal leftLiteral = new Literal();
//        leftLiteral.setType(HybridRebecaTypeSystem.INT_TYPE);
//
//        Literal rightLiteral = new Literal();
//        rightLiteral.setType(HybridRebecaTypeSystem.INT_TYPE);
//
//        // Create BinaryExpression (x = 5)
//        BinaryExpression binaryExpression = new BinaryExpression();
//        binaryExpression.setLeft(leftLiteral);
//        binaryExpression.setRight(rightLiteral);
//        binaryExpression.setOperator("=");
//
//        // Call translate method
//        Object result = translator.translate(binaryExpression, instructions);
//
//        // Verify the generated instruction
//        assertEquals(1, instructions.size());
//        assertTrue(instructions.get(0) instanceof AssignmentInstructionBean);
//
//        AssignmentInstructionBean assignment = (AssignmentInstructionBean) instructions.get(0);
//        assertEquals("x", assignment.getFirstOperand());
//        assertEquals("5", assignment.getSecondOperand());
//    }

    @Test
    void testSetOperandTypesWithDoubleAndIntOperands() {
        Literal leftLiteral = new Literal();
        leftLiteral.setType(HybridRebecaTypeSystem.INT_TYPE);

        Literal rightLiteral = new Literal();
        rightLiteral.setType(HybridRebecaTypeSystem.DOUBLE_TYPE);

        BinaryExpression binaryExpression = new BinaryExpression();
        binaryExpression.setLeft(leftLiteral);
        binaryExpression.setRight(rightLiteral);

        translator.setOperandTypes(binaryExpression);

        assertEquals("int", translator.leftType);
        assertEquals("double", translator.rightType);
    }

}
