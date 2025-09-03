package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StartSetModeInstructionBean extends InstructionBean {

    private String modeName;
    public String getModeName() {
        return modeName;
    }

    public StartSetModeInstructionBean(String modeName) {
        this.modeName = modeName;
    }

    @Override
    public String toString() {
        return "self.setMode" + "(modeName -> "+ this.modeName + ")";
    }
}