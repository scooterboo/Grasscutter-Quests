package emu.grasscutter.scripts.lua_engine;

import lombok.Getter;

public enum ScriptType {
    DATA_STORAGE(false, false),
    STATIC_EXECUTABLE(false, true),
    EXECUTABLE(true, true),
    INCLUDE(true, true);

    @Getter
    private final boolean addDefaultGlobals;
    @Getter
    private final boolean precompile;

    ScriptType(boolean addDefaultGlobals, boolean precompile) {
        this.addDefaultGlobals = addDefaultGlobals;
        this.precompile = precompile;
    }

}
