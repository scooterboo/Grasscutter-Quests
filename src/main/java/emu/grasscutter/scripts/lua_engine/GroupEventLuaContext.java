package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.ScriptArgs;
import lombok.Getter;

public class GroupEventLuaContext implements LuaContext {
    @Getter
    final private SceneGroup groupInstance;
    @Getter
    final private ScriptArgs args;

    final private SceneScriptManager scriptManager;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public GroupEventLuaContext(LuaEngine engine, SceneGroup groupInstance, ScriptArgs args, SceneScriptManager scriptManager) {
        this.groupInstance = groupInstance;
        this.args = args;
        this.scriptManager = scriptManager;
        this.engine = engine;
    }

    public SceneGroup getCurrentGroup() {
        return groupInstance;
    }

    public SceneScriptManager getSceneScriptManager() {
        return scriptManager;
    }
}
