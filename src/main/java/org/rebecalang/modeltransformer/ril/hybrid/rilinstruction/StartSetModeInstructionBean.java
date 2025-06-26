package org.rebecalang.modeltransformer.ril.hybrid.rilinstruction;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StartSetModeInstructionBean extends InstructionBean {

    String modeName;
    public StartSetModeInstructionBean(String modeName) {
        this.modeName = modeName;
    }

    @Override
    public String toString() {
        return "self.setMode" + "(modeName -> "+ this.modeName + ")";
    }
}
