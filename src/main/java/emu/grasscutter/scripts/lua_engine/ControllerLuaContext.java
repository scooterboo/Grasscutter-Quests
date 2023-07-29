package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.game.entity.EntityGadget;
import lombok.Getter;

public class ControllerLuaContext implements LuaContext {
    @Getter
    final private EntityGadget gadget;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public ControllerLuaContext(LuaEngine engine, EntityGadget gadget) {
        this.gadget = gadget;
        this.engine = engine;
    }

    public EntityGadget getEntity() {
        return gadget;
    }
}
