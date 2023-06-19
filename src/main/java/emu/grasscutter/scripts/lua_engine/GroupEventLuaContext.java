package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.ScriptArgs;

public interface GroupEventLuaContext extends LuaContext {
    SceneGroup getCurrentGroup();

    ScriptArgs getArgs();

    SceneScriptManager getSceneScriptManager();
}
