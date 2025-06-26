package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

import java.lang.reflect.Constructor;
import java.util.function.BinaryOperator;

public class StartUnbreakableConditionInstructionBean extends InstructionBean {

    Object leftSide;
    Object rightSide;
    String operator;
    String unbreakableCondition;

    public StartUnbreakableConditionInstructionBean(Object leftSide, Object rightSide, String operator) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.operator = operator;
        String stringBinaryExpression = leftSide + operator + rightSide;
        this.unbreakableCondition = stringBinaryExpression;
    }

    public Object getLeftSide() {
        return leftSide;
    }

    public Object getRightSide() {
        return rightSide;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return this.unbreakableCondition;
    }
}
