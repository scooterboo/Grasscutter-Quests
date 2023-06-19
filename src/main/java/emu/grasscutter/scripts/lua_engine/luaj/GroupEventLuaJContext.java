package emu.grasscutter.scripts.lua_engine.luaj;

import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.ScriptArgs;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.lua_engine.LuaEngine;
import lombok.Getter;

public class GroupEventLuaJContext implements GroupEventLuaContext {

    @Getter
    final private SceneGroup groupInstance;
    @Getter(onMethod = @__(@Override))
    final private ScriptArgs args;

    final private SceneScriptManager scriptManager;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public GroupEventLuaJContext(LuaEngine engine, SceneGroup groupInstance, ScriptArgs args, SceneScriptManager scriptManager) {
        this.groupInstance = groupInstance;
        this.args = args;
        this.scriptManager = scriptManager;
        this.engine = engine;
    }

    @Override
    public SceneGroup getCurrentGroup() {
        return groupInstance;
    }

    @Override
    public SceneScriptManager getSceneScriptManager() {
        return scriptManager;
    }

}
