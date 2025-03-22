package org.rebecalang.modeltransformer.ril.hybrid.translator.expressionTranslator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.AbstractStatementTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartODEInstructionBean;

import java.util.ArrayList;

import static java.util.Objects.isNull;

public class InvariantBlockTranslator extends AbstractStatementTranslator {

    public InvariantBlockTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer, Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(statementTranslatorContainer, expressionTranslatorContainer);
    }

    String computedModeName;
    public void setComputedModeName(String computedModeName) {
        this.computedModeName = computedModeName;
    }

    ArrayList<BinaryExpression> odeBinaryExpressions = new ArrayList<>();
    BinaryExpression odeBinaryExpression;

    public BinaryExpression getOdeBinaryExpression() {
        return odeBinaryExpression;
    }

    @Override
    public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
        if (statement instanceof BlockStatement) {
            BlockStatement blockStatement = (BlockStatement) statement;
            for (Statement insideStatement : blockStatement.getStatements()) {
                if (insideStatement instanceof BinaryExpression) {
                    BinaryExpression binaryExpr = (BinaryExpression) insideStatement;
                    odeBinaryExpression = binaryExpr;

                    String operator = odeBinaryExpression.getOperator();
                    Object leftSide = expressionTranslatorContainer.translate(odeBinaryExpression.getLeft(), instructions);
                    Object rightSide = expressionTranslatorContainer.translate(odeBinaryExpression.getRight(), instructions);
                    System.out.println("ode binary exp is : " + leftSide + operator + rightSide);
                }
            }
        }
    }

}
