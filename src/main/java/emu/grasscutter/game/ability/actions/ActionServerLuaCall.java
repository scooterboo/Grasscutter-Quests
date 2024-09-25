package emu.grasscutter.game.ability.actions;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;

@AbilityAction(AbilityModifierAction.Type.ServerLuaCall)
public class ActionServerLuaCall extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        switch (action.luaCallType) {
            case "FromGroup" -> {
                val scriptManager = target.getScene().getScriptManager();
                val group = scriptManager.getGroupById(target.getGroupId());
                val script = group.getScript();
                val scriptArgs = new ScriptArgs(target.getGroupId(), 0)
                    .setTargetEntityId(target.getId())
                    .setSourceEntityId(target.getId());
                val context = new GroupEventLuaContext(script.getEngine(), group, scriptArgs, scriptManager);
                try {
                    //Todo: read MPBMJIGLEMJ for # of arguments.
                    script.callMethod(action.funcName, context, null);
                } catch (Exception e) {
                    Grasscutter.getLogger().error("Cannot find {} in group {}", context, target.getGroupId());
                    e.printStackTrace();
                }
                return true;
            }
            default -> {
                Grasscutter.getLogger().error("Unimplemented ActionServerLuaCall {}", action.luaCallType);
//                "AbilityGroupSourceGroup"
//                "CurChallengeGroup"
//                "CurGalleryControlGroup"
//                "CurRogueBossGroup"
//                "CurScenePlay"
//                "SpecificGroup"
            }
        }
        return false;
    }
}
