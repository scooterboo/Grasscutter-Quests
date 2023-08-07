package emu.grasscutter.scripts.lua_engine;

import lombok.Getter;

public enum ScriptType {
    GADGET("Gadget"),
    SCENE("Scene"),
    ACTIVITY("Activity"),
    COMMON("Common"),
    QUEST("Quest");
    @Getter
    private final String folderName;

    ScriptType(String folderName) {
        this.folderName = folderName;
    }

}
