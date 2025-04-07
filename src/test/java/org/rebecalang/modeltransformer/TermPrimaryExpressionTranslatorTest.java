package org.rebecalang.modeltransformer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator.getTempVariable;

public class TermPrimaryExpressionTranslatorTest {
    ArrayList<InstructionBean> instructions = new ArrayList<>();

    @AfterEach
    void tearDown() {
        instructions = new ArrayList<>();
        AbstractExpressionTranslator.resetCounter();
    }

    @Test
    void translateContinuousDelayArgsTest() {
        Map<String, Object> passedParameters = new HashMap<>();
        Object lowerBound = new Variable("a");
        Object upperBound = new Variable("a");
        passedParameters.put(TermPrimaryExpressionTranslator.INTERVAL_LOW_KEY, lowerBound);
        passedParameters.put(TermPrimaryExpressionTranslator.INTERVAL_UP_KEY, upperBound);
        int primarySize = instructions.size();
        TermPrimaryExpressionTranslator.translateContinuousDelayArgs(passedParameters, instructions);
        assertEquals(primarySize + 1, instructions.size());

        Variable tempVariable = new Variable("TEMP_EXP$0");
        assertTrue(instructions.get(0).toString().equals("$"+tempVariable+" = ContinuousNonDet {"+lowerBound+", "+upperBound+"}"));
    }

    @Test
    void translateDelayArgsTest() {
        Map<String, Object> passedParameters = new HashMap<>();
        NonDetValue lowerBound = new NonDetValue();
        LinkedList<Object> nonDetValues = new LinkedList<>();
        nonDetValues.add(new Variable("a"));
        nonDetValues.add(new Variable("b"));
        lowerBound.setNondetValues(nonDetValues);
        passedParameters.put(TermPrimaryExpressionTranslator.INTERVAL_LOW_KEY, lowerBound);
        int primarySize = instructions.size();
        TermPrimaryExpressionTranslator.translateDelayArgs(passedParameters, instructions);
        assertEquals(primarySize + 1, instructions.size());

        Variable tempVariable = new Variable("TEMP_EXP$0");
        assertTrue(instructions.get(0).toString().equals("$"+tempVariable+ " = {"+nonDetValues.get(0)+", "+nonDetValues.get(1)+"}"));
    }
}
