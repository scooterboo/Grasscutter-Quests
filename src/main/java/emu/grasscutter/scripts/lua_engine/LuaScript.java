package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.ScriptArgs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

public interface LuaScript {
    boolean hasMethod(@Nonnull String methodName);

    @Nullable
    LuaValue callMethod(@Nonnull String methodName, Object... args) throws ScriptException, NoSuchMethodException;

    void evaluate() throws ScriptException;

    <T> T getGlobalVariable(String name, Class<T> type);

    <T> List<T> getGlobalVariableList(String name, Class<T> type);

    <T> Map<String, T> getGlobalVariableMap(String name, Class<T> type);

    LuaEngine getEngine();

    default ControllerLuaContext getControllerLuaContext(EntityGadget gadget){
        return new ControllerLuaContext(getEngine(), gadget);
    }

    default GroupEventLuaContext getGroupEventLuaContext(SceneGroup sceneGroupInstance, ScriptArgs args, SceneScriptManager scriptManager){
        return new GroupEventLuaContext(getEngine(), sceneGroupInstance, args, scriptManager);
    }
}
