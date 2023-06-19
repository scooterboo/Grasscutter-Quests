package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.game.entity.EntityGadget;

public interface ControllerLuaContext extends LuaContext {
    EntityGadget getEntity();
}
