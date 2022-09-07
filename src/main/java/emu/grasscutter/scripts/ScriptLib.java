package emu.grasscutter.scripts;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.dungeons.challenge.DungeonChallenge;
import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.entity.gadget.GadgetWorktop;
import emu.grasscutter.game.dungeons.challenge.factory.ChallengeFactory;
import emu.grasscutter.game.props.EntityType;
import emu.grasscutter.game.quest.enums.QuestState;
import emu.grasscutter.game.quest.enums.QuestTrigger;
import emu.grasscutter.scripts.data.BaseGroup;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.SceneTrigger;
import emu.grasscutter.server.game.GameServer;
import emu.grasscutter.server.packet.send.PacketCanUseSkillNotify;
import emu.grasscutter.server.packet.send.PacketDungeonShowReminderNotify;
import emu.grasscutter.server.packet.send.PacketWorktopOptionNotify;
import emu.grasscutter.utils.Position;
import io.netty.util.concurrent.FastThreadLocal;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Optional;

import static emu.grasscutter.scripts.constants.EventType.EVENT_TIMER_EVENT;

@SuppressWarnings("unused")
public class ScriptLib {
	public static final Logger logger = LoggerFactory.getLogger(ScriptLib.class);
	private final FastThreadLocal<SceneScriptManager> sceneScriptManager;
	private final FastThreadLocal<BaseGroup> currentGroup;
	public ScriptLib() {
		this.sceneScriptManager = new FastThreadLocal<>();
		this.currentGroup = new FastThreadLocal<>();
	}

	public void setSceneScriptManager(SceneScriptManager sceneScriptManager){
		this.sceneScriptManager.set(sceneScriptManager);
	}

	public void removeSceneScriptManager(){
		this.sceneScriptManager.remove();
	}

	public SceneScriptManager getSceneScriptManager() {
		// normally not null
		return Optional.of(sceneScriptManager.get()).get();
	}

	private String printTable(LuaTable table){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(var meta : table.keys()){
			sb.append(meta).append(":").append(table.get(meta)).append(",");
		}
		sb.append("}");
		return sb.toString();
	}
	public void setCurrentGroup(BaseGroup currentGroup){
		this.currentGroup.set(currentGroup);
	}
	public Optional<BaseGroup> getCurrentGroup(){
		return Optional.of(this.currentGroup.get());
	}
	public void removeCurrentGroup(){
		this.currentGroup.remove();
	}
	public int SetGadgetStateByConfigId(int configId, int gadgetState) {
		logger.debug("[LUA] Call SetGadgetStateByConfigId with {},{}",
				configId,gadgetState);
		Optional<GameEntity> entity = getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(e -> e.getConfigId() == configId).findFirst();

		if (entity.isEmpty()) {
			return 1;
		}

		if (entity.get() instanceof EntityGadget entityGadget) {
			entityGadget.updateState(gadgetState);
			return 0;
		}

		return 1;
	}

	public int SetGroupGadgetStateByConfigId(int groupId, int configId, int gadgetState) {
		logger.debug("[LUA] Call SetGroupGadgetStateByConfigId with {},{},{}",
				groupId,configId,gadgetState);

		getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(e -> e.getGroupId() == groupId)
				.filter(e -> e instanceof EntityGadget)
				.map(e -> (EntityGadget)e)
				.forEach(e -> e.updateState(gadgetState));

		return 0;
	}

	public int SetWorktopOptionsByGroupId(int groupId, int configId, int[] options) {
		logger.debug("[LUA] Call SetWorktopOptionsByGroupId with {},{},{}",
				groupId,configId,options);

		Optional<GameEntity> entity = getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(e -> e.getConfigId() == configId && e.getGroupId() == groupId).findFirst();


		if (entity.isEmpty() || !(entity.get() instanceof EntityGadget gadget)) {
			return 1;
		}

		if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
			return 1;
		}

		worktop.addWorktopOptions(options);
		getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

		return 0;
	}

	public int SetWorktopOptions(LuaTable table){
		logger.debug("[LUA] Call SetWorktopOptions with {}", printTable(table));
		// TODO
		return 0;
	}
	public int DelWorktopOptionByGroupId(int groupId, int configId, int option) {
		logger.debug("[LUA] Call DelWorktopOptionByGroupId with {},{},{}",groupId,configId,option);

		Optional<GameEntity> entity = getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(e -> e.getConfigId() == configId && e.getGroupId() == groupId).findFirst();

		if (entity.isEmpty() || !(entity.get() instanceof EntityGadget gadget)) {
			return 1;
		}

		if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
			return 1;
		}

		worktop.removeWorktopOption(option);
		getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

		return 0;
	}
	public int DelWorktopOption(int var1) {
		logger.debug("[LUA] Call DelWorktopOption with {}",var1);
        //TODO
		return 0;
	}

	// Some fields are guessed
	public int AutoMonsterTide(int challengeIndex, int groupId, Integer[] ordersConfigId, int tideCount, int sceneLimit, int param6) {
		logger.debug("[LUA] Call AutoMonsterTide with {},{},{},{},{},{}",
				challengeIndex,groupId,ordersConfigId,tideCount,sceneLimit,param6);

        var group = getSceneScriptManager().getGroupById(groupId);

		if (group == null || group.monsters == null) {
			return 1;
		}

		this.getSceneScriptManager().startMonsterTideInGroup(group, ordersConfigId, tideCount, sceneLimit);

		return 0;
	}

	public int AddExtraGroupSuite(int groupId, int suite) {
		logger.debug("[LUA] Call AddExtraGroupSuite with {},{}",
				groupId,suite);
        var group = getSceneScriptManager().getGroupById(groupId);

		if (group == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
			return 1;
		}
		// avoid spawn wrong monster
		if(getSceneScriptManager().getScene().getChallenge() != null)
			if(!getSceneScriptManager().getScene().getChallenge().inProgress() ||
					getSceneScriptManager().getScene().getChallenge().getGroup().id != groupId){
			return 0;
		}
		this.getSceneScriptManager().addGroupSuite(group, suiteData);

		return 0;
	}
	public int GoToGroupSuite(int groupId, int suite) {
		logger.debug("[LUA] Call GoToGroupSuite with {},{}",
				groupId,suite);
        var group = getSceneScriptManager().getGroupById(groupId);
		if (group == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
			return 1;
		}

		for(var suiteItem : group.suites){
			if(suiteData == suiteItem){
				continue;
			}
			this.getSceneScriptManager().removeGroupSuite(group, suiteItem);
		}
		this.getSceneScriptManager().addGroupSuite(group, suiteData);

		return 0;
	}
	public int RemoveExtraGroupSuite(int groupId, int suite) {
		logger.debug("[LUA] Call RemoveExtraGroupSuite with {},{}",
				groupId,suite);

        var group = getSceneScriptManager().getGroupById(groupId);
		if (group == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
			return 1;
		}

		this.getSceneScriptManager().removeGroupSuite(group, suiteData);

		return 0;
	}
	public int KillExtraGroupSuite(int groupId, int suite) {
		logger.debug("[LUA] Call KillExtraGroupSuite with {},{}",
				groupId,suite);

        var group = getSceneScriptManager().getGroupById(groupId);
		if (group == null || group.monsters == null) {
			return 1;
		}
		var suiteData = group.getSuiteByIndex(suite);
		if(suiteData == null){
			return 1;
		}

		this.getSceneScriptManager().removeGroupSuite(group, suiteData);

		return 0;
	}
	// param3 (probably time limit for timed dungeons)
	public int ActiveChallenge(int challengeId, int challengeIndex, int timeLimitOrGroupId, int groupId, int objectiveKills, int param5) {
		logger.debug("[LUA] Call ActiveChallenge with {},{},{},{},{},{}",
				challengeId,challengeIndex,timeLimitOrGroupId,groupId,objectiveKills,param5);

		var challenge = ChallengeFactory.getChallenge(
				challengeId,
				challengeIndex,
				timeLimitOrGroupId,
				groupId,
				objectiveKills,
				param5,
				getSceneScriptManager().getScene(),
				getCurrentGroup().get()
				);

		if(challenge == null){
			return 1;
		}

		if(challenge instanceof DungeonChallenge dungeonChallenge){
			// set if tower first stage (6-1)
			dungeonChallenge.setStage(getSceneScriptManager().getVariables().getOrDefault("stage", -1) == 0);
		}

		getSceneScriptManager().getScene().setChallenge(challenge);
		challenge.start();
		return 0;
	}

	public int GetGroupMonsterCountByGroupId(int groupId) {
		logger.debug("[LUA] Call GetGroupMonsterCountByGroupId with {}",
				groupId);
		return (int) getSceneScriptManager().getScene().getEntities().values().stream()
								.filter(e -> e instanceof EntityMonster && e.getGroupId() == groupId)
								.count();
	}

	public int GetGroupVariableValue(String var) {
		logger.debug("[LUA] Call GetGroupVariableValue with {}",
				var);
		return getSceneScriptManager().getVariables().getOrDefault(var, 0);
	}
	public int GetVariableValue(String var1) {
		logger.debug("[LUA] Call GetVariableValue with {}",
				var1);
        //TODO var1 type
		return 0;
	}

	public int SetGroupVariableValue(String var, int value) {
		logger.debug("[LUA] Call SetGroupVariableValue with {},{}",
				var, value);
		getSceneScriptManager().getVariables().put(var, value);
		return 0;
	}

	public LuaValue ChangeGroupVariableValue(String var, int value) {
		logger.debug("[LUA] Call ChangeGroupVariableValue with {},{}",
				var, value);

		getSceneScriptManager().getVariables().put(var, getSceneScriptManager().getVariables().get(var) + value);
		return LuaValue.ZERO;
	}

	/**
	 * Set the actions and triggers to designated group
	 */
	public int RefreshGroup(LuaTable table) {
		logger.debug("[LUA] Call RefreshGroup with {}",
				printTable(table));
		// Kill and Respawn?
		int groupId = table.get("group_id").toint();
		int suite = table.get("suite").toint();

        BaseGroup group = getSceneScriptManager().getGroupById(groupId);

		if (group == null) {
            logger.warn("[LUA] trying to reload unknown group {}", groupId);
			return 1;
		}

		getSceneScriptManager().refreshGroup(group, suite);

		return 0;
	}

	public int GetRegionEntityCount(LuaTable table) {
		logger.debug("[LUA] Call GetRegionEntityCount with {}",
				printTable(table));
		int regionId = table.get("region_eid").toint();
		int entityType = table.get("entity_type").toint();

		var region = this.getSceneScriptManager().getRegionById(regionId);

		if (region == null) {
			return 0;
		}

		return (int) region.getEntities().stream().filter(e -> e >> 24 == entityType).count();
	}

    public void PrintContextLog(String msg) {
        logger.info("[LUA] " + msg);
    }
    public void PrintLog(String msg) {
        logger.info("[LUA] " + msg);
    }
    public void print(String msg) {
        logger.info("[LUA] " + msg);
    }

	public int TowerCountTimeStatus(int isDone, int var2){
		logger.warn("[LUA] Call TowerCountTimeStatus with {},{}",
				isDone,var2);
		// TODO record time
		return 0;
	}

	public int CreateGroupTimerEvent(int groupID, String var2, double var3){
		logger.warn("[LUA] Call unimplemented CreateGroupTimerEvent with {},{}, {}",
				groupID, var2, var3);
        var manager = sceneScriptManager.get();
        var group = manager.getGroupById(groupID);
        if(group == null || group.triggers == null){
            return -1;
        }
        for(SceneTrigger trigger : group.triggers.values()){
            if(trigger.event == EVENT_TIMER_EVENT &&trigger.source.equals(var2)){
                logger.warn("[LUA] Found timer trigger with source {} for group {} : {}",
                    var2, groupID, trigger.name);
                Grasscutter.getGameServer().getScheduler().scheduleDelayedRepeatingTask(new Runnable() {
                    @Override
                    public void run() {
                        manager.callEvent(EVENT_TIMER_EVENT, null);
                    }
                }, (int)var3, (int)var3);
            }
        }
        //currentGroup.get().triggers.values();
		return 0;
	}
    public int CancelGroupTimerEvent(int groupID, String var2){
		logger.warn("[LUA] Call unimplemented CancelGroupTimerEvent with {} {}",
				groupID, var2);
        //TODO implement
		return 0;
	}
	public int GetGroupMonsterCount(){
		logger.debug("[LUA] Call GetGroupMonsterCount ");

		return (int) getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(e -> e instanceof EntityMonster &&
						e.getGroupId() == getCurrentGroup().map(sceneGroup -> sceneGroup.id).orElse(-1))
				.count();
	}
	public int SetMonsterBattleByGroup(int var1, int var2, int var3){
		logger.warn("[LUA] Call unimplemented SetMonsterBattleByGroup with {},{},{}",
				var1,var2,var3);
        //TODO implement
		return 0;
	}
	public int GetGroupSuite(int var1){
		logger.warn("[LUA] Call unimplemented GetGroupSuite with {}",
				var1);
        //TODO implement
		return 0;
	}

	public int CauseDungeonFail(int var1){
		logger.warn("[LUA] Call unimplemented CauseDungeonFail with {}",
				var1);
        //TODO one or zero parameters?
		return 0;
	}
	public int[] GetSceneUidList(){
		logger.warn("[LUA] Call unimplemented GetSceneUidList");
        //TODO
		return new int[]{};
	}
	public int GetSeaLampActivityPhase(){
		logger.warn("[LUA] Call unimplemented GetSeaLampActivityPhase");
        //TODO
		return 0;
	}
	public int GadgetPlayUidOp(int groupId, int gadget_crucible, int var3, int var4, String var5, int var6 ){
		logger.warn("[LUA] Call unimplemented GadgetPlayUidOp with {}, {}, {}, {}, {}, {}", groupId, gadget_crucible, var3, var4, var5, var6);
        //TODO
		return 0;
	}
	public long GetServerTime(){
		logger.debug("[LUA] Call GetServerTime");
        //TODO
		return new Date().getTime();
	}
	public long GetServerTimeByWeek(){
		logger.debug("[LUA] Call GetServerTimeByWeek");
        //TODO time based on week start
		return new Date().getTime();
	}
	public int GetCurTriggerCount(){
		logger.debug("[LUA] Call GetCurTriggerCount");
        //TODO check
		return getSceneScriptManager().getTriggerCount();
	}
	public int GetChannellerSlabLoopDungeonLimitTime(){
		logger.warn("[LUA] Call unimplemented GetChannellerSlabLoopDungeonLimitTime");
        //TODO
		return 0;
	}
	public int IsPlayerAllAvatarDie(int sceneUid){
		logger.warn("[LUA] Call unimplemented IsPlayerAllAvatarDie {}", sceneUid);
        //TODO
		return 0;
	}
	public int sendCloseCommonTipsToClient(){
		logger.warn("[LUA] Call unimplemented sendCloseCommonTipsToClient");
        //TODO
		return 0;
	}
	public int CreateFatherChallenge(int var1, int var2, int var3, LuaTable var4){
		logger.warn("[LUA] Call unimplemented CreateFatherChallenge with {} {} {} {}", var1, var2, var3, var4);
        //TODO var4 object has int success, int fail, bool fail_on_wipe
		return 0;
	}
	public int AttachChildChallenge(int var1, int var2, int var3, int[] var4, LuaTable var5, LuaTable var6){
		logger.warn("[LUA] Call unimplemented AttachChildChallenge with {} {} {} {} {} {}", var1, var2, var3, var4, var5, var6);
        //TODO var6 object has int success, int fail, bool fail_on_wipe
		return 0;
	}
	public int CreateEffigyChallengeMonster(int var1, int[] var2){
		logger.warn("[LUA] Call unimplemented CreateEffigyChallengeMonster with {} {}", var1, var2);
        //TODO
		return 0;
	}
	public int GetEffigyChallengeMonsterLevel(){
		logger.warn("[LUA] Call unimplemented CreateEffigyChallengeMonster");
        //TODO
		return 0;
	}
	public int AddTeamEntityGlobalFloatValue(int[] sceneUidList, String var2, int var3){
		logger.warn("[LUA] Call unimplemented AddTeamEntityGlobalFloatValue with {} {} {}", sceneUidList, var2, var3);
        //TODO
		return 0;
	}
	public int RefreshHuntingClueGroup(){
		logger.warn("[LUA] Call unimplemented RefreshHuntingClueGroup");
        //TODO
		return 0;
	}
	public int GetHuntingMonsterExtraSuiteIndexVec(){
		logger.warn("[LUA] Call unimplemented GetHuntingMonsterExtraSuiteIndexVec");
        //TODO
		return 0;
	}
	public int SetGroupTempValue(String name, int value, LuaTable var3){
		logger.warn("[LUA] Call unimplemented SetGroupTempValue with {} {} {}", name, value, var3);
        //TODO var3 has int group_id
		return 0;
	}
	public int SetPlatformPointArray(int var1, int var2, int[] var3, LuaTable var4){
		logger.warn("[LUA] Call unimplemented SetPlatformPointArray with {} {} {} {}", var1, var2, var3, var4);
        //TODO var4 has int route_type, bool turn_mode
		return 0;
	}

	public int GetEntityIdByConfigId(int configId){
		logger.warn("[LUA] Call GetEntityIdByConfigId with {}", configId);
        //TODO check
        var entity = getSceneScriptManager().getScene().getEntityByConfigId(configId);
		return getSceneScriptManager().getScene().getEntityByConfigId(configId).getId();
	}
	public Position GetPosByEntityId(int entityId){
		logger.warn("[LUA] Call GetPosByEntityId with {}", entityId);
        //TODO check
        var entity = getSceneScriptManager().getScene().getEntityById(entityId);
		return entity != null? entity.getPosition() : new Position();
	}
	public Position GetRotationByEntityId(int entityId){
		logger.debug("[LUA] Call GetRotationByEntityId with {}", entityId);
        //TODO check
        var entity = getSceneScriptManager().getScene().getEntityById(entityId);
        return entity != null? entity.getRotation() : new Position();
	}
    public int FinishExpeditionChallenge(){
        logger.warn("[LUA] unimplemented Call FinishExpeditionChallenge");
        //TODO
        return 0;
    }
    public int InitTimeAxis(String var1, int[] var2, boolean var3){
        logger.warn("[LUA] Call unimplemented InitTimeAxis with {} {} {}", var1, var2, var3);
        //TODO
        return 0;
    }
    public int SetTeamEntityGlobalFloatValue(int[] sceneUidList, String var2, int var3){
        logger.warn("[LUA] Call unimplemented SetTeamEntityGlobalFloatValue with {} {} {}", sceneUidList, var2, var3);
        //TODO
        return 0;
    }
    public int SetTeamServerGlobalValue(int sceneUid, String var2, int var3){
        logger.warn("[LUA] Call unimplemented SetTeamServerGlobalValue with {} {} {}", sceneUid, var2, var3);
        //TODO
        return 0;
    }
    public int GetLanternRiteValue(){
        logger.warn("[LUA] Call unimplemented GetLanternRiteValue");
        //TODO
        return 0;
    }
    public int CreateMonsterFaceAvatar(LuaTable var1){
        logger.warn("[LUA] Call unimplemented CreateMonsterFaceAvatar with {}", var1);
        //TODO var1 contains int entity_id, int[] monsters, int[] ranges, int angle
        return 0;
    }
    public int ChangeToTargetLevelTag(int var1){
        logger.warn("[LUA] Call unimplemented ChangeToTargetLevelTag with {}", var1);
        //TODO
        return 0;
    }
    public int StartHomeGallery(int galleryId, int uid){
        logger.warn("[LUA] Call unimplemented StartHomeGallery with {} {}", galleryId, uid);
        //TODO
        return 0;
    }
    public int StopGallery(int galleryId, boolean var2){
        logger.warn("[LUA] Call unimplemented StopGallery with {} {}", galleryId, var2);
        //TODO
        return 0;
    }
    public int StartGallery(int galleryId){
        logger.warn("[LUA] Call unimplemented StartGallery with {}", galleryId);
        //TODO
        return 0;
    }
    public int UpdatePlayerGalleryScore(int galleryId, LuaTable var2){
        logger.warn("[LUA] Call unimplemented UpdatePlayerGalleryScore with {} {}", galleryId, var2);
        //TODO var2 contains int uid
        return 0;
    }
    public int SetHandballGalleryBallPosAndRot(int galleryId, LuaTable position, LuaTable rotation){
        logger.warn("[LUA] Call unimplemented SetHandballGalleryBallPosAndRot with {} {} {}", galleryId, position, rotation);
        //TODO
        return 0;
    }
    public int UnlockFloatSignal(int groupId, int gadgetSignalId){
        logger.warn("[LUA] Call unimplemented UnlockFloatSignal with {} {}", groupId, gadgetSignalId);
        //TODO
        return 0;
    }
    public int SendServerMessageByLuaKey(String var1, int[] var2){
        logger.warn("[LUA] Call unimplemented SendServerMessageByLuaKey with {} {}", var1, var2);
        //TODO
        return 0;
    }
    public int TryReallocateEntityAuthority(int uid, int endConfig, int var3){
        logger.warn("[LUA] Call unimplemented TryReallocateEntityAuthority with {} {} {}", uid, endConfig, var3);
        //TODO check var3 type
        return 0;
    }
    public int ForceRefreshAuthorityByConfigId(int var1, int uid){
        logger.warn("[LUA] Call unimplemented ForceRefreshAuthorityByConfigId with {} {}", var1, uid);
        //TODO check var3 type
        return 0;
    }
    public int AddPlayerGroupVisionType(int[] uids, int[] var2){
        logger.warn("[LUA] Call unimplemented AddPlayerGroupVisionType with {} {}", uids, var2);
        //TODO
        return 0;
    }
    public int DelPlayerGroupVisionType(int[] uids, int[] var2){
        logger.warn("[LUA] Call unimplemented DelPlayerGroupVisionType with {} {}", uids, var2);
        //TODO
        return 0;
    }
    public int MoveAvatarByPointArray(int uid, int targetId, LuaTable var3, String var4){
        logger.warn("[LUA] Call unimplemented MoveAvatarByPointArray with {} {} {} {}", uid, targetId, var3, var4);
        //TODO var3 contains int speed, var4 is a json string
        return 0;
    }
    public int MovePlayerToPos(LuaTable var1){
        logger.warn("[LUA] Call unimplemented MovePlayerToPos with {}", var1);
        //TODO var1 contains int[] uid_list, Position pos, int radius, Position rot
        return 0;
    }
    public int TransPlayerToPos(LuaTable var1){
        logger.warn("[LUA] Call unimplemented TransPlayerToPos with {}", var1);
        //TODO var1 contains int[] uid_list, Position pos, int radius, Position rot
        return 0;
    }
    public int PlayCutScene(int var1, int var2){
        logger.warn("[LUA] Call unimplemented PlayCutScene with {} {}", var1, var2);
        //TODO
        return 0;
    }
    public int ScenePlaySound(LuaTable var1){
        logger.warn("[LUA] Call unimplemented ScenePlaySound with {}", var1);
        //TODO var1 contains Position play_pos, string sound_name, int play_type, bool is_broadcast
        return 0;
    }
    public int BeginCameraSceneLook(LuaTable var1){
        logger.warn("[LUA] Call unimplemented BeginCameraSceneLook with {}", var1);
        //TODO var1 contains Position look_pos, int duration, bool is_force, bool isBroadcast
        return 0;
    }
    public int ShowReminderRadius(int var1, LuaTable var2, int var3){
        logger.warn("[LUA] Call unimplemented ShowReminderRadius with {} {} {}", var1, var2, var3);
        //TODO var2 is a postion
        return 0;
    }
    public int ActivateDungeonCheckPoint(int var1){
        logger.warn("[LUA] Call unimplemented ActivateDungeonCheckPoint with {}", var1);
        //TODO
        return 0;
    }
    public int SetWeatherAreaState(int var1, int var2){
        logger.warn("[LUA] Call unimplemented SetWeatherAreaState with {} {}", var1, var2);
        //TODO
        return 0;
    }

	public boolean CheckIsInMpMode(){
		logger.debug("[LUA] Call CheckIsInMpMode");
        //TODO check
		return getSceneScriptManager().getScene().getWorld().isMultiplayer();
	}

    public int SetPlatformRouteId(int var2, int routeId){
        logger.warn("[LUA] Call unimplemented SetPlatformRouteId {} {}", var2, routeId);
        //TODO
        // var2 == seelie ID?
        // e.g. scene3_group133003381.lua action_EVENT_ENTER_REGION_381007 line 387
        // or scene3_group133001159.lua action_EVENT_ENTER_REGION_159007 line 387
        return 0;
    }

	public int GetGroupVariableValueByGroup(String name, int groupId){
		logger.debug("[LUA] Call GetGroupVariableValueByGroup with {},{}",
				name,groupId);

		return getSceneScriptManager().getVariables().getOrDefault(name, 0);
	}

	public int SetIsAllowUseSkill(int canUse, int var2){
		logger.debug("[LUA] Call SetIsAllowUseSkill with {},{}",
				canUse,var2);

		getSceneScriptManager().getScene().broadcastPacket(new PacketCanUseSkillNotify(canUse == 1));
		return 0;
	}

	public int KillEntityByConfigId(LuaTable table){
		logger.debug("[LUA] Call KillEntityByConfigId with {}",
				printTable(table));
		var configId = table.get("config_id");
		if(configId == LuaValue.NIL){
			return 1;
		}

		var entity = getSceneScriptManager().getScene().getEntityByConfigId(configId.toint());
		if(entity == null){
			return 0;
		}
		getSceneScriptManager().getScene().killEntity(entity, 0);
		return 0;
	}

	public int SetGroupVariableValueByGroup(String key, int value, int groupId){
		logger.debug("[LUA] Call SetGroupVariableValueByGroup with {},{},{}",
				key,value,groupId);

		getSceneScriptManager().getVariables().put(key, value);
		return 0;
	}

	public int CreateMonster(LuaTable table){
		logger.debug("[LUA] Call CreateMonster with {}",
				printTable(table));
		var configId = table.get("config_id").toint();
		var delayTime = table.get("delay_time").toint();

		if(getCurrentGroup().isEmpty()){
			return 1;
		}

		getSceneScriptManager().spawnMonstersByConfigId(getCurrentGroup().get(), configId, delayTime);
		return 0;
	}

	public int TowerMirrorTeamSetUp(int team, int var1) {
		logger.debug("[LUA] Call TowerMirrorTeamSetUp with {},{}",
				team,var1);

		getSceneScriptManager().unloadCurrentMonsterTide();
		getSceneScriptManager().getScene().getPlayers().get(0).getTowerManager().mirrorTeamSetUp(team-1);

		return 0;
	}

	public int CreateGadget(LuaTable table){
		logger.debug("[LUA] Call CreateGadget with {}",
				printTable(table));
		var configId = table.get("config_id").toint();

		var group = getCurrentGroup();

		if (group.isEmpty()) {
			return 1;
		}

		var gadget = group.get().gadgets.get(configId);
		var entity = getSceneScriptManager().createGadget(group.get().id, group.get().block_id, gadget);

		getSceneScriptManager().addEntity(entity);

		return 0;
	}
	public int CheckRemainGadgetCountByGroupId(LuaTable table){
		logger.debug("[LUA] Call CheckRemainGadgetCountByGroupId with {}",
				printTable(table));
		var groupId = table.get("group_id").toint();

		var count = getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(g -> g instanceof EntityGadget entityGadget && entityGadget.getGroupId() == groupId)
				.count();
		return (int)count;
	}

	public int GetGadgetStateByConfigId(int groupId, int configId){
		logger.debug("[LUA] Call GetGadgetStateByConfigId with {},{}",
				groupId, configId);

		if(groupId == 0){
			groupId = getCurrentGroup().get().id;
		}
		final int realGroupId = groupId;
		var gadget = getSceneScriptManager().getScene().getEntities().values().stream()
				.filter(g -> g instanceof EntityGadget entityGadget && entityGadget.getGroupId() == realGroupId)
				.filter(g -> g.getConfigId() == configId)
				.findFirst();
		if(gadget.isEmpty()){
			return 1;
		}
		return ((EntityGadget)gadget.get()).getState();
	}

	public int MarkPlayerAction(int var1, int var2, int var3, int var4){
		logger.debug("[LUA] Call MarkPlayerAction with {},{},{},{}",
				var1, var2,var3,var4);

		return 0;
	}

	public int AddQuestProgress(String var1){
		logger.debug("[LUA] Call AddQuestProgress with {}",
				var1);

        for(var player : getSceneScriptManager().getScene().getPlayers()){
            player.getQuestManager().triggerEvent(QuestTrigger.QUEST_COND_LUA_NOTIFY, var1);
            player.getQuestManager().triggerEvent(QuestTrigger.QUEST_CONTENT_LUA_NOTIFY, var1);
        }

		return 0;
	}

	/**
	 * change the state of gadget
	 */
	public int ChangeGroupGadget(LuaTable table){
		logger.debug("[LUA] Call ChangeGroupGadget with {}",
				printTable(table));
		var configId = table.get("config_id").toint();
		var state = table.get("state").toint();

		var entity = getSceneScriptManager().getScene().getEntityByConfigId(configId);
		if(entity == null){
			return 1;
		}

		if (entity instanceof EntityGadget entityGadget) {
			entityGadget.updateState(state);
			return 0;
		}

		return 1;
	}

    public int GetEntityType(int entityId){
        return entityId >> 13;
        /*var entity = getSceneScriptManager().getScene().getEntityById(entityId);
        if(entity == null){
            Grasscutter.getLogger().warn("GetEntityType entityId == null {}", entityId);
            return EntityType.None.getValue();
        }
        return entity.getEntityType();*/
    }
    public int GetGadgetIdByEntityId(int entityId){
        var entity = getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityBaseGadget)){
            return 0;
        }

        return entity.getId();
    }
    public int GetMonsterIdByEntityId(int entityId){
        var entity = getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityMonster)){
            return 0;
        }

        return entity.getId();
    }
    public int GetMonsterID(int var1){
       //TODO var1 type
        return 0;
    }

    public int GetQuestState(int entityId, int questId){
        var player = getSceneScriptManager().getScene().getWorld().getHost();

        var quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE.getValue();
        }

        return quest.getState().getValue();
    }

    public int ShowReminder(int reminderId){
        getSceneScriptManager().getScene().broadcastPacket(new PacketDungeonShowReminderNotify(reminderId));
        return 0;
    }

    public int RemoveEntityByConfigId(int groupId, int entityType, int configId){
        logger.debug("[LUA] Call RemoveEntityByConfigId");

        var entity = getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e.getGroupId() == groupId)
            .filter(e -> e.getEntityType() == entityType)
            .filter(e -> e.getConfigId() == configId)
            .findFirst();

        if(entity.isEmpty()){
            return 1;
        }

        getSceneScriptManager().getScene().removeEntity(entity.get());

        return 0;
    }
}
