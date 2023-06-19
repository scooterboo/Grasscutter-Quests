package emu.grasscutter.scripts.lua_engine.luaj;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import emu.grasscutter.scripts.lua_engine.LuaEngine;
import lombok.Getter;

public class ControllerLuaJContext implements ControllerLuaContext {

    @Getter
    final private EntityGadget gadget;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public ControllerLuaJContext(LuaEngine engine, EntityGadget gadget) {
        this.gadget = gadget;
        this.engine = engine;
    }

    @Override
    public EntityGadget getEntity() {
        return gadget;
    }
}
