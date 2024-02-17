package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupGadgetHandler;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;
import org.anime_game_servers.gi_lua.script_lib.handler.activity.SummerTimeScriptHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptLibGroupHandlerProvider implements org.anime_game_servers.gi_lua.script_lib.ScriptLibGroupHandlerProvider<GroupEventLuaContext> {
    // will be replaced by smaller scriptlib_handlers
    final ScriptLibHandler oldHandler = new emu.grasscutter.scripts.ScriptLibHandler();
    final SummerTimeHandler summerTimeHandler = new emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler();
    final GroupGadgetHandler groupGadgetHandler = new GroupGadgetHandler();

    @NotNull
    @Override
    public ScriptLibHandler<GroupEventLuaContext> getScriptLibHandler() {
        return oldHandler;
    }

    @Nullable
    @Override
    public SummerTimeScriptHandler<GroupEventLuaContext> getSummerTimeScriptHandler() {
        return summerTimeHandler;
    }

    @Nullable
    @Override
    public GroupGadgetHandler getGroupGadgetHandler() {
        return groupGadgetHandler;
    }
}
