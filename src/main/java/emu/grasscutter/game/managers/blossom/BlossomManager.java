package emu.grasscutter.game.managers.blossom;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.*;
import emu.grasscutter.data.excels.BlossomRefreshData.*;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.managers.blossom.enums.BlossomRefreshType;
import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.BlossomBriefInfoOuterClass.BlossomBriefInfo;
import emu.grasscutter.net.proto.BlossomChestInfoOuterClass.BlossomChestInfo;
import emu.grasscutter.scripts.constants.EventType;
import emu.grasscutter.scripts.data.ScriptArgs;
import emu.grasscutter.server.packet.send.PacketBlossomBriefInfoNotify;
import emu.grasscutter.server.packet.send.PacketWorldOwnerBlossomScheduleInfoNotify;
import emu.grasscutter.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

@Getter
@SuppressWarnings("SpellCheckingInspection")
public class BlossomManager extends BasePlayerDataManager {
    @Setter private Scene scene;
    /**
     * Holds information of all blossom camps, [Key: GroupId, Value: BlossomSchedule]
     * */
    private final ConcurrentHashMap<Integer, BlossomSchedule> blossomSchedule = new ConcurrentHashMap<>();
    /**
     * Holds information of all spawned chest after finishing blossom challenge,
     * necessary for building chest brief info,
     * [Key: ConfigId, Value: BlossomSchedule]
     * */
    private final ConcurrentHashMap<Integer, BlossomSchedule> spawnedChest = new ConcurrentHashMap<>();

    public BlossomManager(Player player) {
        setPlayer(player);
    }

    /**
     * BlossomBriefInfo proto
     * */
    public List<BlossomBriefInfo> getBriefInfo() {
        return this.blossomSchedule.values().stream()
            .map(BlossomSchedule::toBriefProto)
            .sorted(Comparator.comparing(BlossomBriefInfo::getRefreshId)
                .thenComparing(BlossomBriefInfo::getCircleCampId))
            .toList();
    }

    /**
     * Notify player's(not necessary world owner) all BlossomBriefInfo (all including not spawned)
     * */
    public void notifyPlayerIcon() {
        this.player.sendPacket(new PacketBlossomBriefInfoNotify(getBriefInfo()));
    }

    /**
     * Create all blossom camps and send packets
     * */
    public void onPlayerLogin() {
        buildBlossomSchedule();
        notifyPlayerIcon();
    }

    /**
     * Update Blossom camp's state (started or ended blossom challenge for example), triggered by ScriptLib
     * */
    public boolean setBlossomState(int groupId, int state) {
        return Optional.ofNullable(this.blossomSchedule.get(groupId)).stream()
            .peek(schedule -> schedule.setState(state))
            .peek(schedule -> this.scene.broadcastPacket(
                new PacketWorldOwnerBlossomScheduleInfoNotify(schedule.toScheduleProto())))
            .findFirst()
            .isPresent();
    }

    /**
     * Update Blossom camp's progress (on monster die for example), triggered by ScriptLib
     * */
    public boolean addBlossomProgress(int groupId) {
        return Optional.ofNullable(this.blossomSchedule.get(groupId)).stream()
            .peek(BlossomSchedule::addProgress)
            .peek(schedule -> this.scene.broadcastPacket(
                new PacketWorldOwnerBlossomScheduleInfoNotify(schedule.toScheduleProto())))
            .peek(schedule -> {if (schedule.isFinished()) this.scene.getScriptManager()
                .callEvent(new ScriptArgs(schedule.getGroupId(), EventType.EVENT_BLOSSOM_PROGRESS_FINISH));})
            .findFirst().isPresent();
    }

    /**
     * Get Blossom Chest gadget proto info (after challenge finish)
     * */
    public BlossomChestInfo getChestInfo(int chestConfigId, List<Integer> playersUid) {
        return Optional.ofNullable(this.spawnedChest.get(chestConfigId)).stream()
            .peek(schedule -> schedule.getRemainingUid().addAll(playersUid))
            .map(schedule -> BlossomChestInfo.newBuilder()
                .setResin(schedule.getResin())
                .addAllQualifyUidList(playersUid)
                .addAllRemainUidList(playersUid)
                .setRefreshId(schedule.getRefreshId())
                .setBlossomRefreshType(schedule.getRefreshType().getValue())
                .build())
            .findFirst().orElse(null);
    }

    /**
     * Give player reward after challenge finish
     * */
    public boolean onReward(Player player, @NonNull EntityGadget gadget, boolean useCondensedResin) {
        val schedule = this.spawnedChest.get(gadget.getConfigId());
        // if player is not qualified for the reward
        if (schedule == null || !schedule.getRemainingUid().contains(player.getUid())) return false;

        // give rewards
        val resinManager = player.getResinManager();
        val payable = useCondensedResin ? resinManager.useCondensedResin(1) : resinManager.useResin(schedule.getResin());
        if (!payable) return false;

        val blossomRewards = GameData.getRewardPreviewDataMap().get(schedule.getRewardId());
        if (blossomRewards == null) return false;

        player.getInventory().addItems(Stream.of(blossomRewards.getPreviewItems())
            .map(reward -> new GameItem(reward.getItemId(), reward.getCount() * (useCondensedResin ? 2 : 1)))
            .toList(), ActionReason.OpenBlossomChest);

        schedule.getRemainingUid().remove(player.getUid());
        return true;
    }

    public void buildNextCamp(int groupId) {
        val schedule = this.blossomSchedule.remove(groupId);
        // if player is not qualified for the reward
        if (schedule == null) return;

        Optional.ofNullable(GameData.getBlossomGroupsDataMap().get(schedule.getCircleCampId()))
            .map(BlossomGroupsData::getNextCampId)
            .map(campId -> GameData.getBlossomGroupsDataMap().get(campId.intValue()))
            // if next camp overlaps with existing schedule, get further next camp
            .map(groupsData -> this.blossomSchedule.containsKey(groupsData.getNewGroupId()) ?
                GameData.getBlossomGroupsDataMap().get(groupsData.getNextCampId()) : groupsData).stream()
            .map(groupData -> BlossomSchedule.create(schedule, groupData, 3, getWorldLevel()))
            .filter(Objects::nonNull)
            .peek(newSchedule -> this.blossomSchedule.put(newSchedule.getGroupId(), newSchedule))
            .peek(newSchedule -> this.scene.loadDynamicGroup(newSchedule.getGroupId()))
//            .peek(newSchedule -> Grasscutter.getLogger().info("[BlossomManager] New {}", newSchedule))
            .map(BlossomSchedule::getDecorateGroupId)
            .forEach(this.scene::loadDynamicGroup);
//            .forEach(decorateId -> Grasscutter.getLogger().info("[BlossomManager] New Decorate Group: {}", decorateId));
        notifyPlayerIcon(); // notify all camps again
    }

    /**
     * Rebuild all Blossom Camp gadget (not chest gadget) for scene
     * */
    public void loadBlossomGroup() {
        Optional.ofNullable(this.scene).ifPresent(scene -> this.blossomSchedule.values().stream()
            .filter(schedule -> schedule.getSceneId() == scene.getId())
            .peek(schedule -> scene.loadDynamicGroup(schedule.getGroupId()))
//            .peek(schedule -> Grasscutter.getLogger().info("[Blossom Manager] Loading Blossom Group: {}", schedule.getGroupId()))
            .map(BlossomSchedule::getDecorateGroupId)
            .forEach(scene::loadDynamicGroup));
//            .forEach(decorateId -> Grasscutter.getLogger().info("[Blossom Manager] Loading Decorate Group:{}", decorateId)));
    }

    private int getWorldLevel() {
        return this.player.getWorldLevel(); // maybe should get the owner's?
    }

    private int getPlayerLevel() {
        return this.player.getLevel(); // maybe should get the owner's?
    }

    /**
     * Check if player is qualified to open certain blossom camp
     * */
    private boolean refreshCondMet(@NonNull RefreshCond cond) {
        return switch (cond.getRefreshCondType()) {
            case BLOSSOM_REFRESH_COND_NONE -> true;
            case BLOSSOM_REFRESH_COND_PLAYER_LEVEL_EQUAL_GREATER ->
                cond.getParam().stream().allMatch(p -> getPlayerLevel() >= p);
            case BLOSSOM_REFRESH_COND_PLAYER_LEVEL_LESS_THAN ->
                cond.getParam().stream().allMatch(p -> getPlayerLevel() < p);
            case BLOSSOM_REFRESH_COND_OPEN_STATE ->
                cond.getParam().stream().allMatch(p -> this.player.getProgressManager().getOpenState(p) == 1);
            // case BLOSSOM_REFRESH_COND_ACTIVITY_COND, BLOSSOM_REFRESH_COND_SCENE_TAG_ADDED -> TODO
            default -> false;
        };
    }

    /**
     * Build all blossom camp information
     * */
    private void buildBlossomSchedule() {
        val appendedGroupId = new ArrayList<>(); // counter to avoid repeated groups and section
        val appendedSectionId = new ArrayList<>();

        GameData.getBlossomRefreshDataMap().values().stream()
            .filter(data -> data.getRefreshCondVec().stream().allMatch(this::refreshCondMet))
            .filter(data -> Optional.ofNullable(GameData.getBlossomOpenDataMap().get(data.getCityId()))
                .map(BlossomOpenData::getOpenLevel).filter(openLevel -> getPlayerLevel() >= openLevel).isPresent())
            .filter(data -> data.getRefreshType() != BlossomRefreshType.BLOSSOM_REFRESH_NONE) // unimplemented types
            // get section order for the current city
            .map(data -> {
                val groupList = new ArrayList<BlossomGroupsData>(); // groups that met information
                return Optional.of(GameData.getBlossomSectionOrderDataMap().values().stream()
                        .filter(orderData -> orderData.getCityId() == data.getCityId())
                        .map(BlossomSectionOrderData::getSectionId)
                        .filter(sectionId -> GameData.getBlossomGroupsDataMap().values().stream() // some section does not have groups
                            .anyMatch(groupData -> groupData.getSectionId() == sectionId))
                        .filter(sectionId -> !appendedSectionId.contains(sectionId)) // don't want to get the same section
                        .filter(sectionId -> Optional.of(GameData.getBlossomGroupsDataMap().values().stream()
                                .filter(group -> group.getCityId() == data.getCityId())
                                .filter(group -> group.getSectionId() == sectionId)
                                .filter(group -> group.getRefreshTypeVec().contains(data.getRefreshType().getValue()))
                                .filter(group -> !appendedGroupId.contains(group.getId()))
                                .filter(BlossomGroupsData::isInitialRefresh).toList())
                            .stream().filter(tempGroupList -> tempGroupList.size() >= data.getRefreshCount())
                            .peek(groupList::addAll).findFirst().isPresent()).toList())
                    .stream().filter(sectionList -> !sectionList.isEmpty()).map(Utils::drawRandomListElement)
                    .peek(appendedSectionId::add) // draw a new random section id
                    .map(sectionList -> IntStream.range(0, data.getRefreshCount()).mapToObj(e ->
                            Optional.of(groupList).filter(gl -> !gl.isEmpty()).map(Utils::drawRandomListElement)// draw a new random group
                                .stream()
                                .peek(randomGroup -> appendedGroupId.add(randomGroup.getId())).peek(groupList::remove)
                                .map(randomGroup -> BlossomSchedule.create(data, randomGroup, 3, getWorldLevel())) // build blossom schedule
                                .filter(Objects::nonNull)
                                .findFirst().orElse(null))
                        .filter(Objects::nonNull).toList())
                    .findFirst().orElse(null);
            })
            .filter(Objects::nonNull).flatMap(List::stream)
            .forEach(schedule -> this.blossomSchedule.put(schedule.getGroupId(), schedule));
    }
}
