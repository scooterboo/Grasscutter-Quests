package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;
import org.anime_game_servers.gi_lua.script_lib.handler.activity.SummerTimeScriptHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptLibControllerHandlerProvider implements org.anime_game_servers.gi_lua.script_lib.ScriptLibControllerHandlerProvider<EntityGadget, ControllerLuaContext> {
    final GadgetControllerHandler gadgetControllerHandler = new GadgetControllerHandler();

    @Nullable
    @Override
    public org.anime_game_servers.gi_lua.script_lib.handler.GadgetControllerHandler<EntityGadget, ControllerLuaContext> getGadgetControllerHandler() {
        return gadgetControllerHandler;
    }
}
