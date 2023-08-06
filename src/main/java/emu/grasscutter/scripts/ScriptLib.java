package emu.grasscutter.scripts;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.game.dungeons.challenge.DungeonChallenge;
import emu.grasscutter.game.dungeons.challenge.enums.FatherChallengeProperty;
import emu.grasscutter.game.dungeons.challenge.factory.ChallengeFactory;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.entity.gadget.GadgetWorktop;
import emu.grasscutter.game.entity.gadget.platform.ConfigRoute;
import emu.grasscutter.game.entity.gadget.platform.PointArrayRoute;
import emu.grasscutter.game.props.ClimateType;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.EntityType;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.quest.enums.QuestState;
import emu.grasscutter.game.world.SceneGroupInstance;
import emu.grasscutter.net.proto.EnterTypeOuterClass;
import emu.grasscutter.scripts.constants.EventType;
import emu.grasscutter.scripts.constants.temporary.FlowSuiteOperatePolicy;
import emu.grasscutter.scripts.constants.GroupKillPolicy;
import emu.grasscutter.scripts.constants.temporary.GalleryProgressScoreType;
import emu.grasscutter.scripts.constants.temporary.GalleryProgressScoreUIType;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.SceneObject;
import emu.grasscutter.scripts.data.ScriptArgs;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.lua_engine.LuaContext;
import emu.grasscutter.scripts.lua_engine.LuaTable;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.Position;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static emu.grasscutter.game.props.EnterReason.Lua;
import static emu.grasscutter.scripts.ScriptUtils.luaToPos;
import static emu.grasscutter.scripts.ScriptUtils.posToLua;
import static emu.grasscutter.scripts.constants.GroupKillPolicy.*;

@SuppressWarnings("unused")
public class ScriptLib {
	public static final Logger logger = LoggerFactory.getLogger(ScriptLib.class);

	public ScriptLib() {
	}

    /**
     * Helpers
     */
	private static String printTable(LuaTable table){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(var meta : table.getKeys()){
			sb.append(meta).append(":").append(table.get(meta)).append(",");
		}
		sb.append("}");
		return sb.toString();
	}

    private static void printLog(@Nullable LuaContext context, String source, String msg){
        if(context instanceof GroupEventLuaContext){
            var group = ((GroupEventLuaContext) context).getCurrentGroup();
            logger.debug("[LUA] {} {} {}", source, group.id, msg);
            return;
        } else {
            logger.debug("[LUA] {} {}", source, msg);
        }
    }


    private static GameEntity createGadget(SceneScriptManager sceneScriptManager, int configId, SceneGroup group){
        var gadget = group.gadgets.get(configId);
        var entity = sceneScriptManager.createGadget(group.id, group.block_id, gadget);
        if(entity==null){
            logger.warn("[LUA] Create gadget null with cid: {} gid: {} bid: {}", configId, group.id, group.block_id);
            return null;
        }

        sceneScriptManager.addEntity(entity);
        return entity;
    }


    /**
     * Context free functions
     */

    public static void PrintLog(String msg) {
        printLog(null, "PrintLog", msg);
    }

    public static int GetEntityType(int entityId){
        return entityId >> 24;
    }


    /**
     * Context independent functions
     */
    public static void PrintContextLog(LuaContext context, String msg) {
        printLog(context , "PrintContextLog", msg);
    }


    /**
     * GroupEventLuaContext functions
     */

    public static int SetGadgetStateByConfigId(GroupEventLuaContext context, int configId, int gadgetState) {
		logger.debug("[LUA] Call SetGadgetStateByConfigId with {},{}",
				configId,gadgetState);
		GameEntity entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId);

		if (!(entity instanceof EntityGadget)) {
			return 1;
		}

        ((EntityGadget) entity).updateState(gadgetState);
        return 0;
	}

	public static int SetGroupGadgetStateByConfigId(GroupEventLuaContext context, int groupId, int configId, int gadgetState) {
		logger.debug("[LUA] Call SetGroupGadgetStateByConfigId with {},{},{}",
				groupId,configId,gadgetState);

		val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget)){
            return -1;
        }
        ((EntityGadget) entity).updateState(gadgetState);

		return 0;
	}

	public static int SetWorktopOptionsByGroupId(GroupEventLuaContext context, int groupId, int configId, Object optionsTable) {
        val options = context.getEngine().getTable(optionsTable);
		logger.debug("[LUA] Call SetWorktopOptionsByGroupId with {},{},{}",
				groupId,configId,printTable(options));

        val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId, groupId);

		if (!(entity instanceof EntityGadget gadget)) {
			return 1;
		}

		if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
			return 2;
		}

		worktop.addWorktopOptions(options.getAsIntArray());
		context.getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

		return 0;
	}

	public static int SetWorktopOptions(GroupEventLuaContext context, Object rawTable){
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call SetWorktopOptions with {}", printTable(table));
        val callParams = context.getArgs();
        val group = context.getCurrentGroup();
        val scene = context.getSceneScriptManager().getScene();
        if(callParams == null || group == null){
            return 1;
        }
        val eid = callParams.source_eid;
        val entity = scene.getEntityById(eid);


        int[] worktopOptions = new int[table.getSize()];
        for(int i = 1 ;i<=table.getSize() ;i++){
            worktopOptions[i-1] = table.getInt(i);
        }
        if(!(entity instanceof EntityGadget gadget)|| worktopOptions.length == 0){
            return 2;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 3;
        }

        worktop.addWorktopOptions(worktopOptions);
        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            scene.broadcastPacket(new PacketWorktopOptionNotify(gadget));
        },1);
		return 0;
	}

	public static int DelWorktopOptionByGroupId(GroupEventLuaContext context, int groupId, int configId, int option) {
		logger.debug("[LUA] Call DelWorktopOptionByGroupId with {},{},{}",groupId,configId,option);


        val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId, groupId);

		if (!(entity instanceof EntityGadget gadget)) {
			return 1;
		}

		if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
			return 1;
		}

		worktop.removeWorktopOption(option);
		context.getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

		return 0;
	}
    public static int DelWorktopOption(GroupEventLuaContext context, int var1){
        logger.warn("[LUA] Call unimplemented DelWorktopOption with {}", var1);
        val callParams = context.getArgs();
        val group = context.getCurrentGroup();
        val scene = context.getSceneScriptManager().getScene();
        if(callParams == null || group == null){
            return 1;
        }
        var eid = callParams.source_eid;
        var entity = scene.getEntityById(eid);
        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 2;
        }

        worktop.removeWorktopOption(callParams.param2);

        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            scene.broadcastPacket(new PacketWorktopOptionNotify(gadget));
        },1);

        return 0;
    }

	// Some fields are guessed
	public static int AutoMonsterTide(GroupEventLuaContext context, int challengeIndex, int groupId, Integer[] ordersConfigId, int tideCount, int sceneLimit, int param6) {
		logger.debug("[LUA] Call AutoMonsterTide with {},{},{},{},{},{}",
				challengeIndex,groupId,ordersConfigId,tideCount,sceneLimit,param6);

		SceneGroup group = context.getSceneScriptManager().getGroupById(groupId);

		if (group == null || group.monsters == null) {
			return 1;
		}

		context.getSceneScriptManager().startMonsterTideInGroup(group, ordersConfigId, tideCount, sceneLimit);

		return 0;
	}

    public static int GoToGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call GoToGroupSuite with {},{}",
            groupId,suite);
        val scriptManager = context.getSceneScriptManager();
        SceneGroup group = scriptManager.getGroupById(groupId);
        SceneGroupInstance groupInstance = scriptManager.getGroupInstanceById(groupId);
        if (group == null || groupInstance == null || group.monsters == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

		/*for(var suiteItem : group.suites){
			if(suiteData == suiteItem){
				continue;
			}
			this.getSceneScriptManager().removeGroupSuite(group, suiteItem);
		}*/
        if(groupInstance.getActiveSuiteId() == 0 || groupInstance.getActiveSuiteId() != suite) {
            groupInstance.getDeadEntities().clear();
            scriptManager.addGroupSuite(groupInstance, suiteData);
            groupInstance.setActiveSuiteId(suite);
        }

        return 0;
    }

	public static int AddExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
		logger.debug("[LUA] Call AddExtraGroupSuite with {},{}",
				groupId,suite);
        val scriptManager = context.getSceneScriptManager();
		SceneGroup group = scriptManager.getGroupById(groupId);
        SceneGroupInstance groupInstance = scriptManager.getGroupInstanceById(groupId);

		if (group == null || groupInstance == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
            logger.warn("trying to get suite that doesn't exist: {} {}", groupId, suite);
			return 1;
		}
		scriptManager.addGroupSuite(groupInstance, suiteData);

		return 0;
	}
	public static int RemoveExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
		logger.debug("[LUA] Call RemoveExtraGroupSuite with {},{}",
				groupId,suite);

		SceneGroup group = context.getSceneScriptManager().getGroupById(groupId);
		if (group == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
			return 1;
		}

		context.getSceneScriptManager().removeGroupSuite(group, suiteData);

		return 0;
	}
	public static int KillExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
		logger.debug("[LUA] Call KillExtraGroupSuite with {},{}",
				groupId,suite);

		SceneGroup group = context.getSceneScriptManager().getGroupById(groupId);
		if (group == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
			return 1;
		}

        context.getSceneScriptManager().killGroupSuite(group, suiteData);

		return 0;
	}

    public static int AddExtraFlowSuite(GroupEventLuaContext context, int groupId, int suiteId, int flowSuitePolicy){
        val policy = FlowSuiteOperatePolicy.values()[flowSuitePolicy];
        logger.warn("[LUA] Call unimplemented AddExtraFlowSuite with {} {} {} ({})", groupId, suiteId, policy.name(), flowSuitePolicy);
        // TODO: implement
        return 0;
    }
    public static int RemoveExtraFlowSuite(GroupEventLuaContext context, int groupId, int suiteId, int flowSuitePolicy){
        val policy = FlowSuiteOperatePolicy.values()[flowSuitePolicy];
        logger.warn("[LUA] Call unimplemented RemoveExtraFlowSuite with {} {} {} ({})", groupId, suiteId, policy.name(), flowSuitePolicy);
        // TODO: implement
        return 0;
    }
    public static int KillExtraFlowSuite(GroupEventLuaContext context, int groupId, int suiteId, int flowSuitePolicy){
        val policy = FlowSuiteOperatePolicy.values()[flowSuitePolicy];
        logger.warn("[LUA] Call unimplemented KillExtraFlowSuite with {} {} {} ({})", groupId, suiteId, policy.name(), flowSuitePolicy);
        // TODO: implement
        return 0;
    }

	public static int ActiveChallenge(GroupEventLuaContext context, int challengeId, int challengeIndex, int timeLimitOrGroupId, int groupId, int objectiveKills, int param5) {
		logger.debug("[LUA] Call ActiveChallenge with {},{},{},{},{},{}",
				challengeId,challengeIndex,timeLimitOrGroupId,groupId,objectiveKills,param5);

		var challenge = ChallengeFactory.getChallenge(
				challengeId,
				challengeIndex,
				timeLimitOrGroupId,
				groupId,
				objectiveKills,
				param5,
				context.getSceneScriptManager().getScene(),
				context.getCurrentGroup()
				);

		if(challenge == null){
			return 1;
		}

		if(challenge instanceof DungeonChallenge dungeonChallenge){
			// set if tower first stage (6-1)
			dungeonChallenge.setStage(context.getSceneScriptManager().getVariables(groupId).getOrDefault("stage", -1) == 0);
		}

		context.getSceneScriptManager().getScene().setChallenge(challenge);
		challenge.start();
		return 0;
	}

    public static int StopChallenge(GroupEventLuaContext context, int challengeId, int result) {
        logger.debug("[LUA] Call StopChallenge with ");
        var challenge = context.getSceneScriptManager().getScene().getChallenge();
        if(challenge == null){
            return 1;
        }
        if(challenge.getChallengeId() != challengeId){
            return 2;
        }

        switch (result) {
            case 0 -> challenge.fail();
            case 1 -> challenge.done();
            default -> {
                logger.warn("[LUA] Call StopChallenge with unsupported result {}", result);
                return 3;
            }
        }
        return 0;
    }

    /**
     * Adds or removed time from the challenge
     * TODO verify and implement
     * @param context
     * @param challengeId The active target challenges id
     * @param duration The duration to add or remove
     * @return 0 if success, 1 if no challenge is active, 2 if the challenge id doesn't match the active challenge,
     * 3 if modifying the duration failed
     */
    public static int AddChallengeDuration(GroupEventLuaContext context, int challengeId, int duration) {
        logger.warn("[LUA] Call unimplemented AddChallengeDuration with {},{}", challengeId, duration);
        var challenge = context.getSceneScriptManager().getScene().getChallenge();
        if(challenge == null){
            return 1;
        }
        if(challenge.getChallengeId() != challengeId){
            return 2;
        }
        /*if(!challenge.addDuration(duration)){
            return 3;
        }*/
        return 0;

    }

	public static int GetGroupMonsterCountByGroupId(GroupEventLuaContext context, int groupId) {
		logger.debug("[LUA] Call GetGroupMonsterCountByGroupId with {}",
				groupId);
		return (int) context.getSceneScriptManager().getScene().getEntities().values().stream()
								.filter(e -> e instanceof EntityMonster && e.getGroupId() == groupId)
								.count();
	}

	public static int CreateVariable(GroupEventLuaContext context, String type, Object value) {
		logger.warn("[LUA] Call unimplemented CreateVariable with {} {}",
            type, value);
        //TODO implement
        switch (type){
            case "int":
            default:
                logger.warn("[LUA] Call CreateVariable with unsupported type {} and value {}", type, value);
        }
		return 0;
	}
	public static int SetVariableValue(GroupEventLuaContext context, int var1) {
		logger.warn("[LUA] Call unimplemented SetVariableValue with {}",
				var1);
        //TODO implement var1 type
		return 0;
	}
	public static int GetVariableValue(GroupEventLuaContext context, int var1) {
		logger.warn("[LUA] Call unimplemented GetVariableValue with {}",
				var1);
        //TODO implement var1 type
		return 0;
	}
	public static int GetGroupVariableValue(GroupEventLuaContext context, String var) {
		logger.debug("[LUA] Call GetGroupVariableValue with {}",
				var);
		return context.getSceneScriptManager().getVariables(context.getCurrentGroup().id).getOrDefault(var, 0);
	}

	public static int SetGroupVariableValue(GroupEventLuaContext context, String varName, int value) {
		logger.debug("[LUA] Call SetGroupVariableValue with {},{}",
            varName, value);

        val groupId= context.getCurrentGroup().id;
        val variables = context.getSceneScriptManager().getVariables(groupId);

        val old = variables.getOrDefault(varName, value);
        variables.put(varName, value);
        context.getSceneScriptManager().callEvent(
            new ScriptArgs(groupId, EventType.EVENT_VARIABLE_CHANGE, value, old)
                .setEventSource(varName)
        );
		return 0;
	}

	public static int ChangeGroupVariableValue(GroupEventLuaContext context, String varName, int value) {
		logger.debug("[LUA] Call ChangeGroupVariableValue with {},{}",
            varName, value);

        val groupId= context.getCurrentGroup().id;
        val variables = context.getSceneScriptManager().getVariables(groupId);

        val old = variables.getOrDefault(varName, 0);
        variables.put(varName, old + value);
        logger.debug("[LUA] Call ChangeGroupVariableValue with {},{}",
            old, old+value);
        context.getSceneScriptManager().callEvent(
            new ScriptArgs(groupId, EventType.EVENT_VARIABLE_CHANGE, old+value, old)
                .setEventSource(varName)
        );
		return 0;
	}

	/**
	 * Set the actions and triggers to designated group
	 */
	public static int RefreshGroup(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call RefreshGroup with {}",
				printTable(table));
		// Kill and Respawn?
		val groupId = table.getInt("group_id");
		val suite = table.getInt("suite");

        SceneGroupInstance groupInstance = context.getSceneScriptManager().getGroupInstanceById(groupId);

		if (groupInstance == null) {
            logger.warn("[LUA] trying to refresh unloaded group {}", groupId);
			return 1;
		}

        context.getSceneScriptManager().refreshGroup(groupInstance, suite, false);

		return 0;
	}

	public static int GetRegionEntityCount(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call GetRegionEntityCount with {}",
				printTable(table));
		int regionId = table.getInt("region_eid");
		int entityType = table.getInt("entity_type");

		var region = context.getSceneScriptManager().getRegionById(regionId);

		if (region == null) {
			return 0;
		}

		return (int) region.getEntities().stream().filter(e -> e >> 24 == entityType).count();
	}

	public static int TowerCountTimeStatus(GroupEventLuaContext context, int isDone, int var2){
		logger.debug("[LUA] Call TowerCountTimeStatus with {},{}",
				isDone,var2);
		// TODO record time
		return 0;
	}
	public static int GetGroupMonsterCount(GroupEventLuaContext context){
		logger.debug("[LUA] Call GetGroupMonsterCount ");

		return (int) context.getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(e -> e instanceof EntityMonster &&
						e.getGroupId() == context.getCurrentGroup().id)
				.count();
	}

	public static int SetMonsterBattleByGroup(GroupEventLuaContext context, int configId, int groupId) {
		logger.debug("[LUA] Call SetMonsterBattleByGroup with {} {}",
            configId,groupId);
		// TODO implement scene50008_group250008057.lua uses incomplete group numbers

        val scene = context.getSceneScriptManager().getScene();
        // -> MonsterForceAlertNotify
        var entity = scene.getEntityByConfigId(configId, groupId);
        if(entity != null && entity instanceof EntityMonster monster) {
            scene.broadcastPacket(new PacketMonsterForceAlertNotify(monster.getId()));
        }

		return 0;
	}

	public static int CauseDungeonFail(GroupEventLuaContext context){
		logger.debug("[LUA] Call CauseDungeonFail with");

        var scriptManager = context.getSceneScriptManager();
        if(scriptManager==null){
            return 1;
        }

        var dungeonManager = scriptManager.getScene().getDungeonManager();
        if(dungeonManager==null){
            return 2;
        }

        dungeonManager.failDungeon();
		return 0;
	}

    public static int SetEntityServerGlobalValueByConfigId(GroupEventLuaContext context, int cfgId, String sgvName, int value){
        logger.warn("[LUA] Call unimplemented SetEntityServerGlobalValueByConfigId with {} {} {}", cfgId, sgvName, value);
        //TODO implement
        return 0;
    }

    public static int SetGroupLogicStateValue(GroupEventLuaContext context, String sgvName, int value){
        logger.warn("[LUA] Call unimplemented SetGroupLogicStateValue with {} {}", sgvName, value);
        //TODO implement
        return 0;
    }

	public static int GetGroupVariableValueByGroup(GroupEventLuaContext context, String name, int groupId){
		logger.debug("[LUA] Call GetGroupVariableValueByGroup with {},{}",
				name,groupId);

		return context.getSceneScriptManager().getVariables(groupId).getOrDefault(name, 0);
	}
	public static int ChangeGroupVariableValueByGroup(GroupEventLuaContext context, String name, int value, int groupId){
		logger.debug("[LUA] Call ChangeGroupVariableValueByGroup with {},{}",
				name,groupId);
        //TODO test
        context.getSceneScriptManager().getVariables(groupId).put(name, value);
		return 0;
	}

	public static int SetIsAllowUseSkill(GroupEventLuaContext context, int canUse){
		logger.debug("[LUA] Call SetIsAllowUseSkill with {}",
				canUse);

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCanUseSkillNotify(canUse == 1));
		return 0;
	}

	public static int KillEntityByConfigId(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call KillEntityByConfigId with {}",
				printTable(table));
        return killEntityByConfigId(context.getSceneScriptManager().getScene().getScriptManager(), table);
	}

	public static int KillEntityByConfigId(ControllerLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call KillEntityByConfigId with {} on Controller",
				printTable(table));
        return killEntityByConfigId(context.getGadget().getScene().getScriptManager(), table);
	}

    private static int killEntityByConfigId(SceneScriptManager scriptManager, LuaTable luaTable){
        var configId = luaTable.optInt("config_id", -1);
        if(configId == -1){
            return 1;
        }

        var entity = scriptManager.getScene().getEntityByConfigId(configId);
        if(entity == null){
            return 0;
        }
        scriptManager.getScene().killEntity(entity, 0);
        return 0;
    }

	public static int SetGroupVariableValueByGroup(GroupEventLuaContext context, String key, int value, int groupId){
		logger.debug("[LUA] Call SetGroupVariableValueByGroup with {},{},{}",
				key,value,groupId);

        context.getSceneScriptManager().getVariables(groupId).put(key, value);
        // TODO should this maybe trigger a variable changed?
		return 0;
	}

	public static int CreateMonster(GroupEventLuaContext context, Object rawTable){
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call CreateMonster with {}",
				printTable(table));
		val configId = table.getInt("config_id");
		val delayTime = table.getInt("delay_time");
        val group = context.getCurrentGroup();

		context.getSceneScriptManager().spawnMonstersByConfigId(group, configId, delayTime);
		return 0;
	}

	public static int TowerMirrorTeamSetUp(GroupEventLuaContext context, int team, int var1) {
		logger.debug("[LUA] Call TowerMirrorTeamSetUp with {},{}",
				team,var1);

        context.getSceneScriptManager().unloadCurrentMonsterTide();
        context.getSceneScriptManager().getScene().getPlayers().get(0).getTowerManager().mirrorTeamSetUp(team-1);

		return 0;
	}

	public static int CreateGadget(GroupEventLuaContext context, Object rawTable){
         val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call CreateGadget with {}",
				printTable(table));
		val configId = table.getInt("config_id");
		val group = context.getCurrentGroup();

        createGadget(context.getSceneScriptManager(), configId, group);

		return 0;
	}

	public static int CheckRemainGadgetCountByGroupId(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call CheckRemainGadgetCountByGroupId with {}",
				printTable(table));
		var groupId = table.getInt("group_id");

		var count = context.getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(g -> g instanceof EntityGadget entityGadget && entityGadget.getGroupId() == groupId)
				.count();
		return (int)count;
	}

	public static int GetGadgetStateByConfigId(GroupEventLuaContext context, int groupId, int configId){
		logger.debug("[LUA] Call GetGadgetStateByConfigId with {},{}",
				groupId, configId);

        val scene = context.getSceneScriptManager().getScene();
        val gadget = groupId == 0 ? scene.getEntityByConfigId(configId) : scene.getEntityByConfigId(configId, groupId);
        if(!(gadget instanceof EntityGadget)){
            return -1;
        }
        return ((EntityGadget)gadget).getState();
	}

	public static int MarkPlayerAction(GroupEventLuaContext context, int var1, int var2, int var3){
		logger.debug("[LUA] Call MarkPlayerAction with {},{},{}",
				var1, var2,var3);

		return 0;
	}

	public static int AddQuestProgress(GroupEventLuaContext context, String eventNotifyName){
		logger.debug("[LUA] Call AddQuestProgress with {}",
            eventNotifyName);

        for(var player : context.getSceneScriptManager().getScene().getPlayers()){
            player.getQuestManager().queueEvent(QuestCond.QUEST_COND_LUA_NOTIFY, eventNotifyName);
            player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_LUA_NOTIFY, eventNotifyName);
        }

		return 0;
	}

	/**
	 * change the state of gadget
	 */
	public static int ChangeGroupGadget(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
		logger.debug("[LUA] Call ChangeGroupGadget with {}",
				printTable(table));
		var configId = table.getInt("config_id");
		var state = table.getInt("state");

		var entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId);
		if(entity == null){
			return 1;
		}

		if (entity instanceof EntityGadget entityGadget) {
			entityGadget.updateState(state);
			return 0;
		}

		return 1;
	}

    public static int GetSceneOwnerUid(GroupEventLuaContext context){
        return context.getSceneScriptManager().getScene().getWorld().getHost().getUid();
    }

    public static int GetHostQuestState(GroupEventLuaContext context, int questId){
        val player = context.getSceneScriptManager().getScene().getWorld().getHost();

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE.getValue();
        }

        return quest.getState().getValue();
    }

    public static int GetQuestState(GroupEventLuaContext context, int entityId, int questId){
        val player = context.getSceneScriptManager().getScene().getWorld().getHost();

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE.getValue();
        }

        return quest.getState().getValue();
    }

    public static int ShowReminder(GroupEventLuaContext context, int reminderId){
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketDungeonShowReminderNotify(reminderId));
        return 0;
    }

    public static int RemoveEntityByConfigId(GroupEventLuaContext context, int groupId, int entityType, int configId){
        logger.debug("[LUA] Call RemoveEntityByConfigId");

        val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId, groupId);

        if(entity == null || entity.getEntityType() != entityType){
            return 1;
        }

        context.getSceneScriptManager().getScene().removeEntity(entity);

        return 0;
    }

    public static int CreateGroupTimerEvent(GroupEventLuaContext context, int groupID, String source, double time) {
        return  context.getSceneScriptManager().createGroupTimerEvent(groupID, source, time);
    }

    public static int CancelGroupTimerEvent(GroupEventLuaContext context, int groupID, String source) {
        return  context.getSceneScriptManager().cancelGroupTimerEvent(groupID, source);
    }

    public static int GetGroupSuite(GroupEventLuaContext context, int groupId) {
        //logger.warn("[LUA] Call GetGroupSuite with {}", groupID);
        var instance = context.getSceneScriptManager().getGroupInstanceById(groupId);
        if(instance != null) return instance.getActiveSuiteId();
        return 0;
    }
    public static int SetGroupReplaceable(GroupEventLuaContext context, int groupId, boolean value) {
        logger.warn("[LUA] Call SetGroupReplaceable with {} {}", groupId, value);

        var group = context.getSceneScriptManager().getGroupById(groupId);
        if(group != null && group.is_replaceable != null) {
            group.is_replaceable.value = value;
            return 0;
        }
        return 1;
    }

    public static Object GetSceneUidList(GroupEventLuaContext context){
        logger.warn("[LUA] Call unchecked GetSceneUidList");
        //TODO check
        val scriptManager = context.getSceneScriptManager();
        val result = context.getEngine().createTable();
        if(scriptManager == null){
            return result;
        }
        var players = scriptManager.getScene().getPlayers();
        for(int i = 0; i< players.size(); i++){
            result.set(Integer.toString(i+1), players.get(i).getUid());
        }
        return result.getRawTable();
    }

    public static int GetSeaLampActivityPhase(GroupEventLuaContext context){
        logger.warn("[LUA] Call unimplemented GetSeaLampActivityPhase");
        //TODO implement
        return 0;
    }
    public static int GadgetPlayUidOp(GroupEventLuaContext context, int groupId, int gadget_crucible, int var3, int var4, String var5, int var6 ){
        logger.warn("[LUA] Call unimplemented GadgetPlayUidOp with {}, {}, {}, {}, {}, {}", groupId, gadget_crucible, var3, var4, var5, var6);
        //TODO implement
        return 0;
    }
    public static long GetServerTime(GroupEventLuaContext context){
        logger.debug("[LUA] Call GetServerTime");
        //TODO check
        return new Date().getTime();
    }
    public static long GetServerTimeByWeek(GroupEventLuaContext context){
        logger.debug("[LUA] Call GetServerTimeByWeek");
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }
    public static int GetCurTriggerCount(GroupEventLuaContext context){
        logger.debug("[LUA] Call GetCurTriggerCount");
        //TODO check
        return context.getSceneScriptManager().getTriggerCount();
    }
    public static int GetChannellerSlabLoopDungeonLimitTime(GroupEventLuaContext context){
        logger.warn("[LUA] Call unimplemented GetChannellerSlabLoopDungeonLimitTime");
        //TODO implement
        return 0;
    }
    public static boolean IsPlayerAllAvatarDie(GroupEventLuaContext context, int sceneUid){
        logger.warn("[LUA] Call unimplemented IsPlayerAllAvatarDie {}", sceneUid);
        var playerEntities = context.getSceneScriptManager().getScene().getEntities().values().stream().filter(e -> e.getEntityType() == EntityType.Avatar.getValue()).toList();
        for (GameEntity p : playerEntities){
            var player = (EntityAvatar)p;
            if(player.isAlive()){
                return false;
            }
        }
        //TODO check
        return true;
    }

    public static int sendShowCommonTipsToClient(GroupEventLuaContext context, String title, String content, int closeTime) {
        logger.debug("[LUA] Call sendShowCommonTipsToClient with {}, {}, {}", title, content, closeTime);
         context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowCommonTipsNotify(title, content, closeTime));
        return 0;
    }

    public static int sendCloseCommonTipsToClient(GroupEventLuaContext context){
        logger.debug("[LUA] Call unimplemented sendCloseCommonTipsToClient");
         context.getSceneScriptManager().getScene().broadcastPacket(new PacketCloseCommonTipsNotify());
        return 0;
    }

    public static int CreateFatherChallenge(GroupEventLuaContext context, int var1, int var2, int var3, Object var4Table){
        val var4 = context.getEngine().getTable(var4Table);
        logger.warn("[LUA] Call unimplemented CreateFatherChallenge with {} {} {} {}", var1, var2, var3, var4);
        //TODO implement var4 object has int success, int fail, bool fail_on_wipe
        return 0;
    }
    public static int StartFatherChallenge(GroupEventLuaContext context, int var1){
        logger.warn("[LUA] Call unimplemented StartFatherChallenge with {}", var1);
        //TODO implement
        return 0;
    }
    public static int ModifyFatherChallengeProperty(GroupEventLuaContext context, int challengeId, int propertyTypeIndex, int value){
        val propertyType = FatherChallengeProperty.values()[propertyTypeIndex];
        logger.warn("[LUA] Call unimplemented ModifyFatherChallengeProperty with {} {} {}", challengeId, propertyType.name(), value);
        //TODO implement
        return 0;
    }
    public static int AttachChildChallenge(GroupEventLuaContext context, int var1, int var2, int var3, Object var4Tabke, Object var5Table, Object var6Table){
        val var4 = context.getEngine().getTable(var4Tabke);
        val var5 = context.getEngine().getTable(var5Table);
        val var6 = context.getEngine().getTable(var6Table);
        logger.warn("[LUA] Call unimplemented AttachChildChallenge with {} {} {} {} {} {}", var1, var2, var3,
            printTable(var4), printTable(var5), printTable(var6));
        //TODO implement var6 object has int success, int fail, bool fail_on_wipe
        return 0;
    }
    public static int CreateEffigyChallengeMonster(GroupEventLuaContext context, int var1, Object var2Table){
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented CreateEffigyChallengeMonster with {} {}", var1, printTable(var2));
        //TODO implement
        return 0;
    }
    public static int GetEffigyChallengeMonsterLevel(GroupEventLuaContext context){
        logger.warn("[LUA] Call unimplemented CreateEffigyChallengeMonster");
        //TODO implement
        return 0;
    }
    public static int AddTeamEntityGlobalFloatValue(GroupEventLuaContext context, Object sceneUidListTable, String var2, int var3){
        val sceneUidList = context.getEngine().getTable(sceneUidListTable);
        logger.warn("[LUA] Call unimplemented AddTeamEntityGlobalFloatValue with {} {} {}", printTable(sceneUidList), var2, var3);
        //TODO implement
        return 0;
    }
    public static int CreateBlossomChestByGroupId(GroupEventLuaContext context, int groupId, int var2){
        logger.warn("[LUA] Call unimplemented SetBlossomScheduleStateByGroupId with {} {}", groupId, var2);
        //TODO implement
        return 0;
    }
    public static int SetBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId, int scene){
        logger.warn("[LUA] Call unimplemented SetBlossomScheduleStateByGroupId with {} {}", groupId, scene);
        //TODO implement scene is guessed
        return 0;
    }
    public static int RefreshBlossomGroup(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
        logger.warn("[LUA] Call unimplemented RefreshBlossomGroup with {}", printTable(table));
        //TODO implement var3 has int group_id, int suite, bool exclude_prev
        return 0;
    }
    public static int RefreshBlossomDropRewardByGroupId(GroupEventLuaContext context, int groupId){
        logger.warn("[LUA] Call unimplemented RefreshBlossomDropRewardByGroupId with {}", groupId);
        //TODO implement
        return 0;
    }
    public static int AddBlossomScheduleProgressByGroupId(GroupEventLuaContext context, int groupId){
        logger.warn("[LUA] Call unimplemented AddBlossomScheduleProgressByGroupId with {}", groupId);
        //TODO implement
        return 0;
    }
    public static int RefreshHuntingClueGroup(GroupEventLuaContext context){
        logger.warn("[LUA] Call unimplemented RefreshHuntingClueGroup"); //TODO: Much many calls o this garbages the log
        //TODO implement
        return 0;
    }
    public static int GetHuntingMonsterExtraSuiteIndexVec(GroupEventLuaContext context){
        logger.warn("[LUA] Call unimplemented GetHuntingMonsterExtraSuiteIndexVec");
        //TODO implement
        return 0;
    }
    public static int SetGroupTempValue(GroupEventLuaContext context, String name, int value, Object var3Table) {
        val var3 = context.getEngine().getTable(var3Table);
        logger.warn("[LUA] Call unimplemented SetGroupTempValue with {} {} {}", name, value, printTable(var3));
        //TODO implement var3 has int group_id
        return 0;
    }
    public static int GetGroupTempValue(GroupEventLuaContext context, String name, Object var2Table) {
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented GetGroupTempValue with {} {}", name, printTable(var2));
        //TODO implement var3 has int group_id
        return 0;
    }

    public static int FinishExpeditionChallenge(GroupEventLuaContext context){
        logger.warn("[LUA] unimplemented Call FinishExpeditionChallenge");
        //TODO implement
        return 0;
    }
    public static int ExpeditionChallengeEnterRegion(GroupEventLuaContext context, boolean var1){
        logger.warn("[LUA] unimplemented Call ExpeditionChallengeEnterRegion with {}", var1);
        //TODO implement
        return 0;
    }

    public static int StartSealBattle(GroupEventLuaContext context, int gadgetId, Object var2Table) {
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] unimplemented Call StartSealBattle with {} {}", gadgetId, printTable(var2));
        //TODO implement var2 containt int radius, int battle_time, int monster_group_id, int default_kill_charge, int auto_charge, int auto_decline, int max_energy, SealBattleType battleType
        // for type KILL_MONSTER watch group monster_group_id and afterwards trigger EVENT_SEAL_BATTLE_END with the result in param2
        return 0;
    }

    public static int InitTimeAxis(GroupEventLuaContext context, String var1, Object var2Table, boolean var3){
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented InitTimeAxis with {} {} {}", var1, printTable(var2), var3);
        //TODO implement var1 == name? var2 == delay? var3 == should loop?
        return 0;
    }
    public static int EndTimeAxis(GroupEventLuaContext context, String var1){
        logger.warn("[LUA] Call unimplemented EndTimeAxis with {}", var1);
        //TODO implement var1 == name?
        return 0;
    }

    public static int SetTeamEntityGlobalFloatValue(GroupEventLuaContext context, Object sceneUidListTable, String var2, int var3){
        val sceneUidList = context.getEngine().getTable(sceneUidListTable);
        logger.warn("[LUA] Call unimplemented SetTeamEntityGlobalFloatValue with {} {} {}", printTable(sceneUidList), var2, var3);
        //TODO implement
        return 0;
    }

    public static int SetTeamServerGlobalValue(GroupEventLuaContext context, int sceneUid, String var2, int var3){
        logger.warn("[LUA] Call unimplemented SetTeamServerGlobalValue with {} {} {}", sceneUid, var2, var3);
        //TODO implement
        return 0;
    }

    public static int AddTeamServerGlobalValue(GroupEventLuaContext context, int ownerId, String sgvName, int value){
        logger.warn("[LUA] Call unimplemented AddTeamServerGlobalValue with {} {} {}", ownerId, sgvName, value);
        //TODO implement
        return 0;
    }

    public static int GetTeamServerGlobalValue(GroupEventLuaContext context, int ownerId, String sgvName, int value){
        logger.warn("[LUA] Call unimplemented GetTeamServerGlobalValue with {} {} {}", ownerId, sgvName, value);
        //TODO implement
        return 0;
    }

    public static int GetLanternRiteValue(GroupEventLuaContext context){
        logger.warn("[LUA] Call unimplemented GetLanternRiteValue");
        //TODO implement
        return 0;
    }

    public static int CreateMonsterFaceAvatar(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
        logger.warn("[LUA] Call unimplemented CreateMonsterFaceAvatar with {}", printTable(table));
        //TODO implement var1 contains int entity_id, int[] monsters (cfgIds), int[] ranges, int angle
        return 0;
    }

    public static int ChangeToTargetLevelTag(GroupEventLuaContext context, int var1){
        logger.warn("[LUA] Call unimplemented ChangeToTargetLevelTag with {}", var1);
        //TODO implement
        return 0;
    }

    public static int AddSceneTag(GroupEventLuaContext context, int sceneId, int sceneTagId){
        logger.warn("[LUA] Call unimplemented AddSceneTag with {}, {}", sceneId, sceneTagId);
        //TODO implement
        return 0;
    }

    public static int DelSceneTag(GroupEventLuaContext context, int sceneId, int sceneTagId){
        logger.warn("[LUA] Call unimplemented DelSceneTag with {}, {}", sceneId, sceneTagId);
        //TODO implement
        return 0;
    }

    public static boolean CheckSceneTag(GroupEventLuaContext context, int sceneId, int sceneTagId){
        logger.warn("[LUA] Call unimplemented CheckSceneTag with {}, {}", sceneId, sceneTagId);
        //TODO implement
        return false;
    }
    public static int StartHomeGallery(GroupEventLuaContext context, int galleryId, int uid){
        logger.warn("[LUA] Call unimplemented StartHomeGallery with {} {}", galleryId, uid);
        //TODO implement
        return 0;
    }

    public static int StartGallery(GroupEventLuaContext context, int galleryId){
        logger.warn("[LUA] Call unimplemented StartGallery with {}", galleryId);
        //TODO implement
        return 0;
    }

    public static int StopGallery(GroupEventLuaContext context, int galleryId, boolean var2){
        logger.warn("[LUA] Call unimplemented StopGallery with {} {}", galleryId, var2);
        //TODO implement
        return 0;
    }

    public static int UpdatePlayerGalleryScore(GroupEventLuaContext context, int galleryId, Object var2Table) {
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented UpdatePlayerGalleryScore with {} {}", galleryId, printTable(var2));
        //TODO implement var2 contains int uid
        return 0;
    }
    public static int InitGalleryProgressScore(GroupEventLuaContext context, String name, int galleryId, Object progressTable,
                                               int scoreUiTypeIndex, int scoreTypeIndex) {
        val progress = context.getEngine().getTable(progressTable);
        val uiScoreType = GalleryProgressScoreUIType.values()[scoreUiTypeIndex];
        val scoreType = GalleryProgressScoreType.values()[scoreTypeIndex];
        logger.warn("[LUA] Call unimplemented InitGalleryProgressScore with {} {} {} {}({}) {}({})", name, galleryId, printTable(progress),
            uiScoreType.name(), scoreUiTypeIndex, scoreType.name(), scoreTypeIndex);
        //TODO implement
        return 0;
    }
    public static int InitGalleryProgressWithScore(GroupEventLuaContext context, String name, int galleryId, Object progressTable,
                                               int maxProgress, int scoreUiTypeIndex, int scoreTypeIndex) {
        val progress = context.getEngine().getTable(progressTable);
        val uiScoreType = GalleryProgressScoreUIType.values()[scoreUiTypeIndex];
        val scoreType = GalleryProgressScoreType.values()[scoreTypeIndex];
        logger.warn("[LUA] Call unimplemented InitGalleryProgressWithScore with {} {} {} {} {}({}) {}({})", name, galleryId, printTable(progress),
            maxProgress, uiScoreType.name(), scoreUiTypeIndex, scoreType.name(), scoreTypeIndex);
        //TODO implement
        return 0;
    }
    public static int AddGalleryProgressScore(GroupEventLuaContext context, String name, int galleryId, int score) {
        logger.warn("[LUA] Call unimplemented AddGalleryProgressScore with {} {} {}", name, galleryId, score);
        //TODO implement
        return 0;
    }
    public static int GetGalleryProgressScore(GroupEventLuaContext context, String name, int galleryId) {
        logger.warn("[LUA] Call unimplemented GetGalleryProgressScore with {} {}", name, galleryId);
        //TODO implement
        return 0;
    }
    public static int SetHandballGalleryBallPosAndRot(GroupEventLuaContext context, int galleryId, Object positionTable, Object rotationTable){
        val position = context.getEngine().getTable(positionTable);
        val rotation = context.getEngine().getTable(rotationTable);
        logger.warn("[LUA] Call unimplemented SetHandballGalleryBallPosAndRot with {} {} {}", galleryId, printTable(position), printTable(rotation));
        //TODO implement
        return 0;
    }

    public static int UnlockFloatSignal(GroupEventLuaContext context, int groupId, int gadgetSignalId){
        logger.warn("[LUA] Call unimplemented UnlockFloatSignal with {} {}", groupId, gadgetSignalId);
        //TODO implement
        return 0;
    }

    public static int SendServerMessageByLuaKey(GroupEventLuaContext context, String var1, Object var2Table){
        var var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented SendServerMessageByLuaKey with {} {}", var1, printTable(var2));
        //TODO implement
        return 0;
    }

    public static int TryReallocateEntityAuthority(GroupEventLuaContext context, int uid, int endConfig, int var3){
        logger.warn("[LUA] Call unimplemented TryReallocateEntityAuthority with {} {} {}", uid, endConfig, var3);
        //TODO implement check var3 type
        return 0;
    }

    public static int ForceRefreshAuthorityByConfigId(GroupEventLuaContext context, int var1, int uid){
        logger.warn("[LUA] Call unimplemented ForceRefreshAuthorityByConfigId with {} {}", var1, uid);
        //TODO implement check var3 type
        return 0;
    }

    public static int AddPlayerGroupVisionType(GroupEventLuaContext context, Object uidsTable, Object var2Table){
        val uids = context.getEngine().getTable(uidsTable);
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented AddPlayerGroupVisionType with {} {}", printTable(uids), printTable(var2));
        //TODO implement
        return 0;
    }

    public static int DelPlayerGroupVisionType(GroupEventLuaContext context, Object uidsTable, Object var2Table){
        val uids = context.getEngine().getTable(uidsTable);
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented DelPlayerGroupVisionType with {} {}", printTable(uids), printTable(var2));
        //TODO implement
        return 0;
    }

    public static int MoveAvatarByPointArray(GroupEventLuaContext context, int uid, int targetId, Object var3Table, String var4){
        val var3 = context.getEngine().getTable(var3Table);
        logger.warn("[LUA] Call unimplemented MoveAvatarByPointArray with {} {} {} {}", uid, targetId, printTable(var3), var4);
        //TODO implement var3 contains int speed, var4 is a json string
        return 0;
    }

    public static int MovePlayerToPos(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
        logger.warn("[LUA] Call unchecked MovePlayerToPos with {}", printTable(table));
        //TODO implement var1 contains int[] uid_list, Position pos, int radius, Position rot
        return TransPlayerToPos(context, table); // todo this is probably not a full scene reload
    }

    public static int TransPlayerToPos(GroupEventLuaContext context, Object rawTable) {
        val var1 = context.getEngine().getTable(rawTable);
        logger.warn("[LUA] Call unchecked TransPlayerToPos with {}", printTable(var1));
        //TODO implement var1 contains int[] uid_list, Position pos, int radius, Position rot
        val targetsTable = var1.getTable("uid_list");
        val pos = var1.getTable("pos");
        val rot = var1.getTable("rot");
        val radius = var1.optInt("radius", -1);

        if(targetsTable==null || targetsTable.getSize()==0 || pos == null){
            return 1;
        }
        ArrayList<Integer> targets = new ArrayList<>(targetsTable.getSize());
        for (int i = 1; i <= targetsTable.getSize(); i++) {
            targets.add(targetsTable.getInt(i));
        }


        var scriptManager = context.getSceneScriptManager();
        if(scriptManager==null || !pos.has("x") || !pos.has("y") || !pos.has("z")){
            return 2;
        }

        val x = pos.getFloat("x");
        val y = pos.getFloat("y");
        val z = pos.getFloat("z");

        var targetPos = new Position(x, y, z);

        var scene = scriptManager.getScene();
        scene.getPlayers().stream().filter(p -> targets.contains(p.getUid())).forEach(p -> {
            scene.removePlayer(p);
            scene.addPlayer(p);
            p.getPosition().set(targetPos);

            // Teleport packet
            p.sendPacket(new PacketPlayerEnterSceneNotify(p, EnterTypeOuterClass.EnterType.ENTER_TYPE_GOTO, Lua, scene.getId(), targetPos));
        });
        return 0;
    }

    public static int PlayCutScene(GroupEventLuaContext context, int cutsceneId, int var2){
        logger.warn("[LUA] Call unimplemented PlayCutScene with {} {}", cutsceneId, var2);
         context.getSceneScriptManager().getScene().broadcastPacket(new PacketCutsceneBeginNotify(cutsceneId));
        //TODO implement
        return 0;
    }

    public static int PlayCutSceneWithParam(GroupEventLuaContext context, int cutsceneId, int var2, Object var3Table){
        val var3 = context.getEngine().getTable(var3Table);
        logger.warn("[LUA] Call unimplemented PlayCutSceneWithParam with {} {} {}", cutsceneId, var2, printTable(var3));
        //TODO implement
        return 0;
    }

    public static int ScenePlaySound(GroupEventLuaContext context, Object soundInfoTable) {
        val soundInfo = context.getEngine().getTable(soundInfoTable);
        logger.debug("[LUA] Call unimplemented ScenePlaySound with {}", printTable(soundInfo));
        val soundName = soundInfo.optString("sound_name", null);
        val playType = soundInfo.optInt("play_type", 0);
        val isBroadcast = soundInfo.optBoolean("is_broadcast", true);
        val luaPlayPosition = soundInfo.getTable("play_pos");

        val playPosition = luaToPos(luaPlayPosition);
         context.getSceneScriptManager().getScene().broadcastPacket(new PacketScenePlayerSoundNotify(playPosition, soundName, playType));
        return 0;
    }

    public static int BeginCameraSceneLook(GroupEventLuaContext context, Object sceneLookParamsTable){
        val sceneLookParams = context.getEngine().getTable(sceneLookParamsTable);
        logger.debug("[LUA] Call BeginCameraSceneLook with {}", printTable(sceneLookParams));
        val luaLookPos = sceneLookParams.getTable("look_pos");

        val cameraParams = new PacketBeginCameraSceneLookNotify.CameraSceneLookNotify();
        cameraParams.setLookPos(luaToPos(luaLookPos));
        if(sceneLookParams.has("duration")) {
            cameraParams.setDuration(sceneLookParams.getFloat("duration"));
        }
        if(sceneLookParams.has("is_force")) {
            cameraParams.setForce(sceneLookParams.getBoolean("is_force"));
        }
        if(sceneLookParams.has("is_allow_input")) {
            cameraParams.setAllowInput(sceneLookParams.getBoolean("is_allow_input"));
        }
        if (sceneLookParams.has("is_set_follow_pos")) {
            cameraParams.setSetFollowPos(sceneLookParams.getBoolean("is_set_follow_pos"));
        }
        if (sceneLookParams.has("is_force_walk")) {
            cameraParams.setForceWalk(sceneLookParams.getBoolean("is_force_walk"));
        }
        if (sceneLookParams.has("is_change_play_mode")) {
            cameraParams.setChangePlayMode(sceneLookParams.getBoolean("is_change_play_mode"));
        }
        if(sceneLookParams.has("is_recover_keep_current")) {
            cameraParams.setRecoverKeepCurrent(sceneLookParams.getBoolean("is_recover_keep_current"));
        }
        if (sceneLookParams.has("is_set_screen_xy")) {
            val isSetScreenXY = sceneLookParams.getBoolean("is_set_screen_xy");
            cameraParams.setScreenXY(isSetScreenXY);
            if(isSetScreenXY && sceneLookParams.has("screen_x")) {
                cameraParams.setScreenX(sceneLookParams.getFloat("screen_x"));
            }
            if(isSetScreenXY && sceneLookParams.has("screen_y")) {
                cameraParams.setScreenX(sceneLookParams.getFloat("screen_y"));
            }
        }
        if (sceneLookParams.has("is_set_follow_pos")) {
            val isSetFollowPos = sceneLookParams.getBoolean("is_set_follow_pos");
            cameraParams.setSetFollowPos(isSetFollowPos);
            if(isSetFollowPos && sceneLookParams.has("follow_pos")) {
                val luaFollowPos = sceneLookParams.getTable("follow_pos");
                cameraParams.setFollowPos(luaToPos(luaFollowPos));
            }
        }
        if(sceneLookParams.has("is_broadcast")) {
            // TODO cameraParams.setBroadcast(sceneLookParams.getBoolean("is_broadcast"));
        }

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketBeginCameraSceneLookNotify(cameraParams));
        return 0;
    }

    public static int ShowReminderRadius(GroupEventLuaContext context, int var1, Object var2Table, int var3){
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented ShowReminderRadius with {} {} {}", var1, printTable(var2), var3);
        //TODO implement var2 is a postion
        return 0;
    }
    public static int ShowClientGuide(GroupEventLuaContext context, String guideName){
        logger.debug("[LUA] Call unimplemented ShowClientGuide with {}", guideName);
        if (GameData.getGuideTriggerDataStringMap().get(guideName) != null) {
            // if should handle by open state, dont send packet here
            // not entirely sure what return value is about
            // probably not needing this check statement here since the value comes from
            // the lua script
            return 1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowClientGuideNotify(guideName));
        return 0;
    }

    /**
     * Activates a dungeon checkpoint.
     * @param context a group event lua context
     * @param pointId the scene point id of the dungeon checkpoint
     * @return 0 if successful, 1 if dungeon manager is null, 2 if dungeon manager failed to activate the checkpoint
     */
    public static int ActivateDungeonCheckPoint(GroupEventLuaContext context, int pointId){
        logger.debug("[LUA] Call untested ActivateDungeonCheckPoint with {}", pointId);
        var dungeonManager = context.getSceneScriptManager().getScene().getDungeonManager();
        if(dungeonManager == null){
            return 1;
        }
        return dungeonManager.activateRespawnPoint(pointId) ? 0:2;
    }

    //TODO check
    public static int SetWeatherAreaState(GroupEventLuaContext context, int var1, int var2){
        logger.warn("[LUA] Call unimplemented SetWeatherAreaState with {} {}", var1, var2);
        context.getSceneScriptManager().getScene().getPlayers().forEach(p -> p.setWeather(var1, ClimateType.getTypeByValue(var2)));
        return 0;
    }

    public static int EnterWeatherArea(GroupEventLuaContext context, int weatherAreaId){
        logger.warn("[LUA] Call unimplemented EnterWeatherArea with {}", weatherAreaId);
        //TODO implement
        return 0;
    }

    //TODO check
    public static boolean CheckIsInMpMode(GroupEventLuaContext context){
        logger.debug("[LUA] Call CheckIsInMpMode");
        return context.getSceneScriptManager().getScene().getWorld().isMultiplayer();
    }

    /**
     * TODO properly implement
     * var3 might contain the next point, sometimes is a single int, sometimes multiple ints as array
     * var4 has RouteType route_type, bool turn_mode
     */
    public static int SetPlatformPointArray(GroupEventLuaContext context, int entityConfigId, int pointArrayId, Object var3Table, Object var4Table){
        val var3 = context.getEngine().getTable(var3Table);
        val var4 = context.getEngine().getTable(var4Table);
        logger.warn("[LUA] Call unimplemented SetPlatformPointArray with {} {} {} {}", entityConfigId, pointArrayId, printTable(var3), printTable(var4));

        val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(entityConfigId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(!(routeConfig instanceof PointArrayRoute)){
            routeConfig = new PointArrayRoute((entityGadget).getMetaGadget());
            entityGadget.setRouteConfig(routeConfig);
        }

        val configRoute = (PointArrayRoute) routeConfig;
        //TODO also check targetPoint/targetPoints
        if(configRoute.getPointArrayId() == pointArrayId){
            return -1;
        }

        configRoute.setPointArrayId(pointArrayId);
        //TODO also set targetPoint/targetPoints
         context.getSceneScriptManager().getScene().broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));

        return -1;
    }

    //TODO check
    public static int SetPlatformRouteId(GroupEventLuaContext context, int entityConfigId, int routeId){
        logger.info("[LUA] Call SetPlatformRouteId {} {}", entityConfigId, routeId);

        val entity =  context.getSceneScriptManager().getScene().getEntityByConfigId(entityConfigId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(!(routeConfig instanceof ConfigRoute)){
            routeConfig = new ConfigRoute((entityGadget).getMetaGadget());
            entityGadget.setRouteConfig(routeConfig);
        }

        val configRoute = (ConfigRoute) routeConfig;
        if(configRoute.getRouteId() == routeId){
            return 0;
        }

        configRoute.setRouteId(routeId);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    //TODO check
    public static int StartPlatform(GroupEventLuaContext context, int configId){
        logger.info("[LUA] Call StartPlatform {} ", configId);

        val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId);

        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.startPlatform() ? 0 : 2;
    }

    //TODO check
    public static int StopPlatform(GroupEventLuaContext context, int configId){
        logger.info("[LUA] Call StopPlatform {} ", configId);
        val entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.stopPlatform() ? 0 : 2;
    }

    public static int CreateChannellerSlabCampRewardGadget(GroupEventLuaContext context, int configId){
        logger.warn("[LUA] Call unimplemented CreateChannellerSlabCampRewardGadget {}", configId);
        var group = context.getCurrentGroup();
        if(group == null){
            return 1;
        }
        createGadget(context.getSceneScriptManager(), configId, group);
        //TODO implement fully
        return 0;
    }

    public static int AssignPlayerShowTemplateReminder(GroupEventLuaContext context, int var1, Object var2Table){
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented AssignPlayerShowTemplateReminder {} {}", var1, var2);
        //TODO implement var2 contains LuaTable param_uid_vec, LuaTable param_vec int[] uid_vec
        return 0;
    }

    public static int RevokePlayerShowTemplateReminder(GroupEventLuaContext context, int var1, Object var2Table){
        val var2 = context.getEngine().getTable(var2Table);
        logger.warn("[LUA] Call unimplemented AssignPlayerShowTemplateReminder {} {}", var1, var2);
        //TODO implement
        return 0;
    }

    public static int UnlockForce(GroupEventLuaContext context, int force){
        logger.debug("[LUA] Call UnlockForce {}", force);
        context.getSceneScriptManager().getScene().unlockForce(force);
        return 0;
    }

    public static int LockForce(GroupEventLuaContext context, int force){
        logger.debug("[LUA] Call LockForce {}", force);
        context.getSceneScriptManager().getScene().lockForce(force);
        return 0;
    }

    public static int KillGroupEntity(GroupEventLuaContext context, Object rawTable) {
        val table = context.getEngine().getTable(rawTable);
        logger.debug("[LUA] Call KillGroupEntity with {}", printTable(table));
        //TODO check
        val sceneManager = context.getSceneScriptManager();
        val groupId = table.optInt("group_id", -1);
        val killPolicyId = table.optInt("kill_policy", -1);
        if(groupId == -1 || sceneManager == null){
            return 1;
        }


        val group = sceneManager.getGroupById(groupId);
        if (group == null) {
            return 2;
        }

        if(killPolicyId!=-1 ){
            var killPolicy = GroupKillPolicy.values()[killPolicyId];
            return killGroupEntityWithPolicy(sceneManager, group, killPolicy);
        }

        return killGroupEntityWithTable(sceneManager, group, table);
    }

    private static int killGroupEntityWithTable(SceneScriptManager sceneScriptManager, SceneGroup group, LuaTable lists){
        // get targets
        val monsterList = lists.getTable("monsters");
        val gadgetList = lists.getTable("gadgets");
        val monsterSize = monsterList != null ? monsterList.getSize() : 0;
        val gadgetSize = gadgetList != null ? gadgetList.getSize() : 0;

        int[] targets = new int[monsterSize + gadgetSize];
        int targetsIndex = 0;
        if(monsterList != null) {
            for (int i = 1; i <= monsterSize; i++, targetsIndex++) {
                targets[targetsIndex] = monsterList.optInt(i, -1);
            }
        }
        if(gadgetList != null) {
            for (int i = 1; i <= gadgetSize; i++, targetsIndex++) {
                targets[targetsIndex] = gadgetList.optInt(i, -1);
            }
        }

        // kill targets if exists
        for(int cfgId : targets){
            var entity = sceneScriptManager.getScene().getEntityByConfigId(cfgId, group.id);
            if (entity == null || cfgId == 0) {
                continue;
            }
            sceneScriptManager.getScene().killEntity(entity, 0);
        }
        return 0;
    }

    private static int killGroupEntityWithPolicy(SceneScriptManager sceneScriptManager,SceneGroup group, GroupKillPolicy killPolicy){
        // get targets
        var targets = new ArrayList<SceneObject>();
        if(killPolicy==GROUP_KILL_MONSTER || killPolicy == GROUP_KILL_ALL){
            targets.addAll(group.monsters.values());
        }
        if(killPolicy == GROUP_KILL_GADGET || killPolicy == GROUP_KILL_ALL) {
            targets.addAll(group.gadgets.values());
        }

        // kill targets if exists
        targets.forEach(o -> {
            var entity = sceneScriptManager.getScene().getEntityByConfigId(o.config_id);
            if (entity == null) {
                return;
            }
            sceneScriptManager.getScene().killEntity(entity, 0);
        });
        return 0;
    }

    public static int GetGadgetIdByEntityId(GroupEventLuaContext context, int entityId){
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityBaseGadget)){
            return 0;
        }
        return ((EntityBaseGadget) entity).getGadgetId();
    }
    public static int GetMonsterIdByEntityId(GroupEventLuaContext context, int entityId){
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityMonster)){
            return 0;
        }
        return ((EntityMonster) entity).getMonsterData().getId();
    }
    public static int GetMonsterConfigId(GroupEventLuaContext context, int entityId){
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityMonster)){
            return 0;
        }
        return entity.getConfigId();
    }
    public static int GetMonsterID(GroupEventLuaContext context, int var1){
        //TODO implement var1 type
        return 0;
    }
    public static int GetEntityIdByConfigId(GroupEventLuaContext context, int configId){
        logger.warn("[LUA] Call GetEntityIdByConfigId with {}", configId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityByConfigId(configId);
        return entity != null ? entity.getId() : 0;
    }
    public static int GetAvatarEntityIdByUid(GroupEventLuaContext context, int uid){
        logger.warn("[LUA] Call unchecked GetAvatarEntityIdByUid with {}", uid);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityAvatar && ((EntityAvatar)e).getPlayer().getUid() == uid)
            .findFirst();
        return entity.map(GameEntity::getId).orElse(0);
    }



    public static Object GetPosByEntityId(GroupEventLuaContext context, int entityId){
        logger.warn("[LUA] Call unchecked GetPosByEntityId with {}", entityId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        return posToLua(entity != null? entity.getPosition() : null, context.getEngine()).getRawTable();
    }

    public static Object GetRotationByEntityId(GroupEventLuaContext context, int entityId){
        logger.debug("[LUA] Call unchecked GetRotationByEntityId with {}", entityId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        return posToLua(entity != null? entity.getRotation() : null, context.getEngine()).getRawTable();
    }

    public static Object GetActivityOpenAndCloseTimeByScheduleId(GroupEventLuaContext context, int scheduleId){
        logger.debug("[LUA] Call GetActivityOpenAndCloseTimeByScheduleId with {}", scheduleId);

        var result = context.getEngine().createTable();
        var activityConfig = ActivityManager.getScheduleActivityConfigMap().get(scheduleId);

        if(activityConfig != null){
            result.set(1, activityConfig.getBeginTime().getTime());
            result.set(2, activityConfig.getEndTime().getTime());
        }

        return result.getRawTable();
    }

    public static int GetGameHour(GroupEventLuaContext context){
        return context.getSceneScriptManager().getScene().getWorld().getGameTimeHours();
    }

    public static int ActivateGroupLinkBundle(GroupEventLuaContext context, int groupId){
        logger.warn("[LUA] Call unimplemented ActivateGroupLinkBundle with {}", groupId);
        //TODO implement
        return 0;
    }
    public static int ActivateGroupLinkBundleByBundleId(GroupEventLuaContext context, int bundleId){
        logger.warn("[LUA] Call unimplemented ActivateGroupLinkBundleByBundleId with {}", bundleId);
        //TODO implement
        return 0;
    }

    /**
     * TODO implement
     * @param context
     * @param givingId The id if the giving element found in [GivingData]
     * @param groupId The groupdId of the group containing the gadget
     * @param gadgetCfgId The gadgets target configId
     * @return 0 if success, something else if failed
     */
    public static int ActiveGadgetItemGiving(GroupEventLuaContext context, int givingId, int groupId, int gadgetCfgId){
        logger.warn("[LUA] Call unimplemented ActiveGadgetItemGiving with {} {} {}", givingId, groupId, gadgetCfgId);
        return 0;
    }

    public static int AddChessBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int uid, int pointsToAdd){
        logger.warn("[LUA] Call unimplemented AddChessBuildingPoints with {} {} {} {}", groupId, param2, uid, pointsToAdd);
        //TODO implement
        return 0;
    }

    public static int AddEntityGlobalFloatValueByConfigId(GroupEventLuaContext context, Object param1Table, String param2, int param3){
        val param1 = context.getEngine().getTable(param1Table);
        logger.warn("[LUA] Call unimplemented AddEntityGlobalFloatValueByConfigId with {} {} {}", printTable(param1), param2, param3);
        //TODO implement
        return 0;
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2  probably the name of the data field
     * @param param3
     * @return
     */
    public static int AddExhibitionAccumulableData(GroupEventLuaContext context, int uid, String param2, int param3){
        logger.warn("[LUA] Call unimplemented AddExhibitionAccumulableData with {} {} {}", uid, param2, param3);
        return 0;
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2 probably the name of the data field
     * @param param3
     * @param param4Table contains the fields "play_type" is part of the enum [ExhibitionPlayType] and "gallery_id"
     * @return
     */
    public static int AddExhibitionAccumulableDataAfterSuccess(GroupEventLuaContext context, int uid, String param2, int param3, Object param4Table){
        val param4 = context.getEngine().getTable(param4Table);
        logger.warn("[LUA] Call unimplemented AddExhibitionAccumulableDataAfterSuccess with {} {} {} {}", uid, param2, param3, printTable(param4));
        return 0;
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2  probably the name of the data field
     * @param param3
     * @return
     */
    public static int AddExhibitionReplaceableData(GroupEventLuaContext context, int uid, String param2, int param3){
        logger.warn("[LUA] Call unimplemented AddExhibitionReplaceableData with {} {} {}", uid, param2, param3);
        return 0;
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2 probably the name of the data field
     * @param param3
     * @param param4Table contains the fields "play_type" is part of the enum [ExhibitionPlayType] and "gallery_id"
     * @return
     */
    public static int AddExhibitionReplaceableDataAfterSuccess(GroupEventLuaContext context, int uid, String param2, int param3, Object param4Table){
        val param4 = context.getEngine().getTable(param4Table);
        logger.warn("[LUA] Call unimplemented AddExhibitionReplaceableDataAfterSuccess with {} {} {} {}", uid, param2, param3, printTable(param4));
        return 0;
    }

    public static int AddFleurFairMultistagePlayBuffEnergy(GroupEventLuaContext context, int groupId, int param2, int uid, int bonusId){
        logger.warn("[LUA] Call unimplemented AddFleurFairMultistagePlayBuffEnergy with {} {} {} {}", groupId, param2, uid, bonusId);
        //TODO implement
        return 0;
    }

    public static int AddGadgetPlayProgress(GroupEventLuaContext context, int param1, int param2, int progressChange){
        logger.warn("[LUA] Call unimplemented AddGadgetPlayProgress with {} {} {}", param1, param2, progressChange);
        //TODO implement
        return 0;
    }

    public static int AddIrodoriChessBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int points){
        logger.warn("[LUA] Call unimplemented AddIrodoriChessBuildingPoints with {} {} {}", groupId, param2, points);
        //TODO implement
        return 0;
    }
    public static int AddIrodoriChessTowerServerGlobalValue(GroupEventLuaContext context, int groupId, int param2, int param3, int delta){
        logger.warn("[LUA] Call unimplemented AddIrodoriChessTowerServerGlobalValue with {} {} {} {}", groupId, param2, param3, delta);
        //TODO implement
        return 0;
    }
    public static int AddMechanicusBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int uid, int delta){
        logger.warn("[LUA] Call unimplemented AddIrodoriChessTowerServerGlobalValue with {} {} {} {}", groupId, param2, uid, delta);
        //TODO implement
        return 0;
    }

    public static int AddRegionRecycleProgress(GroupEventLuaContext context, int regionId, int delta){
        logger.warn("[LUA] Call unimplemented AddRegionRecycleProgress with {} {}", regionId, delta);
        //TODO implement
        return 0;
    }
    public static int AddRegionSearchProgress(GroupEventLuaContext context, int regionId, int delta){
        logger.warn("[LUA] Call unimplemented AddRegionSearchProgress with {} {}", regionId, delta);
        //TODO implement
        return 0;
    }
    public static int AddRegionalPlayVarValue(GroupEventLuaContext context, int uid, int regionId, int delta){
        logger.warn("[LUA] Call unimplemented AddRegionalPlayVarValue with {} {} {}", uid, regionId, delta);
        //TODO implement
        return 0;
    }
    public static int AddSceneMultiStagePlayUidValue(GroupEventLuaContext context, int groupId, int param2, String param3, int uid, int param5){
        logger.warn("[LUA] Call unimplemented AddSceneMultiStagePlayUidValue with {} {} {} {} {}", groupId, param2, param3, uid, param5);
        //TODO implement
        return 0;
    }
    public static int AddScenePlayBattleProgress(GroupEventLuaContext context, int groupId, int progress){
        logger.warn("[LUA] Call unimplemented AddScenePlayBattleProgress with {} {}", groupId, progress);
        //TODO implement
        return 0;
    }

    /**
     * TODO implement
     * @param context
     * @param param1Table contains the following fields: param_index:int, param_list:Table, param_uid_list:Table,
     *                    duration:int, target_uid_list:Table
     * @return
     */
    public static int AssignPlayerUidOpNotify(GroupEventLuaContext context, Object param1Table){
        val param1 = context.getEngine().getTable(param1Table);
        logger.warn("[LUA] Call unimplemented AssignPlayerUidOpNotify with {}", printTable(param1));
        return 0;
    }


    /**
     * Methods used in EntityControllers/using ControllerLuaContext
     */

    public static int SetGadgetState(ControllerLuaContext context, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.updateState(gadgetState);
        return 0;
    }

    public static int GetGadgetState(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getState();
    }

    public static int ResetGadgetState(ControllerLuaContext context, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.getPosition().set(gadget.getBornPos());
        gadget.getRotation().set(gadget.getBornRot());
        gadget.setStartValue(0);
        gadget.setStopValue(0);
        gadget.updateState(gadgetState);
        return 0;
    }

    public static int SetGearStartValue(ControllerLuaContext context, int startValue) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.setStartValue(startValue);
        return 0;
    }

    public static int GetGearStartValue(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getStartValue();
    }

    public static int SetGearStopValue(ControllerLuaContext context, int startValue) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.setStopValue(startValue);
        return 0;
    }

    public static int GetGearStopValue(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getStopValue();
    }

    public static int GetGadgetStateBeginTime(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getTicksSinceChange();
    }

    public static int GetContextGadgetConfigId(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getConfigId();
    }

    public static int GetContextGroupId(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getGroupId();
    }

    public static int SetGadgetEnableInteract(ControllerLuaContext context, int groupId, int configId, boolean enable) {
        val gadget = context.getEntity();
        if(gadget.getGroupId() != groupId || gadget.getConfigId() != configId) return -1;

        gadget.setInteractEnabled(enable);

        return 0;
    }

    public static int DropSubfield(ControllerLuaContext context, Object paramsTable) {
        val gadget = context.getEntity();
        val params = context.getEngine().getTable(paramsTable);
        String subfield_name = params.getString("subfield_name");

        gadget.dropSubfield(subfield_name);

        return -1;
    }

    public static int[] GetGatherConfigIdList(ControllerLuaContext context) {
        val gadget = context.getEntity();
        val children = gadget.getChildren();

        val configIds = new int[children.size()];
        for(int i = 0; i < children.size(); i++) {
            configIds[i] = children.get(i).getConfigId();
        }

        return configIds;
    }
}
