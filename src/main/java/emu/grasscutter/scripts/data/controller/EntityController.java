package emu.grasscutter.scripts.data.controller;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.ElementType;
import emu.grasscutter.scripts.ScriptLib;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import emu.grasscutter.scripts.lua_engine.LuaValue;
import emu.grasscutter.scripts.lua_engine.LuaScript;
import emu.grasscutter.scripts.lua_engine.mock_results.IntLuaValue;

import javax.annotation.Nonnull;
import javax.script.ScriptException;
import java.util.Arrays;

public class EntityController {
    private transient LuaScript entityController;

    public EntityController(LuaScript entityControllers){
        this.entityController =  entityControllers;
    }

    public void onBeHurt(GameEntity entity, ElementType elementType, boolean isHost) {
        callControllerScriptFunc(entity, "OnBeHurt", elementType.getValue(), 0, isHost);
    }

    public void onDie(GameEntity entity, ElementType elementType) {
        callControllerScriptFunc(entity, "OnDie", elementType.getValue(), 0);
    }

    public void onTimer(GameEntity entity, int now) {
        callControllerScriptFunc(entity, "OnTimer", now);
    }

    public int onClientExecuteRequest(GameEntity entity, int param1, int param2, int param3) {
        Grasscutter.getLogger().debug("Request on {}, {}: {}", entity.getGroupId(), param1, entity.getPosition().toString());
        LuaValue value = callControllerScriptFunc(entity, "OnClientExecuteReq", param1, param2, param3);
        if(value.isInteger() && value.asInteger() == 1) return 1;

        return 0;
    }

    // TODO actual execution should probably be handle by EntityControllerScriptManager
    private LuaValue callControllerScriptFunc(GameEntity entity, @Nonnull String funcName, Object... args) {

        LuaValue ret = IntLuaValue.ONE;

        if (entityController.hasMethod(funcName)) {
            try {
                ControllerLuaContext context = entityController.getControllerLuaContext((EntityGadget) entity);
                Object[] newArgs = new Object[args.length + 1];
                newArgs[0] = context;
                System.arraycopy(args, 0, newArgs, 1, args.length);
                return entityController.callMethod(funcName, newArgs);
            } catch (RuntimeException | ScriptException | NoSuchMethodException error) {
                ScriptLib.logger.error("[LUA] call function failed in gadget {} with {} ,{}",
                    entity.getEntityTypeId(), funcName, Arrays.toString(args), error);
                ret = IntLuaValue.N_ONE;
            }
        } else if (!funcName.equals("OnTimer")) {
            ScriptLib.logger.error("[LUA] unknown func in gadget {} with {} {}",
                entity.getEntityTypeId(), funcName, Arrays.toString(args));
        }
        return ret;
    }
}
