package emu.grasscutter.game.activity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.ActivityWatcherData;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.server.packet.send.PacketActivityUpdateWatcherNotify;
import emu.grasscutter.utils.JsonUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityWatcherInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity("activities")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
@AllArgsConstructor
@NoArgsConstructor
public class PlayerActivityData {
    @Id
    String id;
    int uid;
    int activityId;
    Map<Integer, WatcherInfo> watcherInfoMap;
    /**
     * the detail data of each type of activity (Json format)
     */
    String detail;
    @Transient Player player;
    @Transient ActivityHandler activityHandler;
    public void save() {
        DatabaseHelper.savePlayerActivityData(this);
    }

    public static PlayerActivityData getByPlayer(Player player, int activityId) {
        return DatabaseHelper.getPlayerActivityData(player.getUid(), activityId);
    }

    public synchronized void addWatcherProgress(int watcherId) {
        var watcherInfo = watcherInfoMap.get(watcherId);
        if (watcherInfo == null) {
            return;
        }

        if (watcherInfo.isFinished()) {
            return;
        }

        watcherInfo.curProgress++;
        getPlayer().sendPacket(new PacketActivityUpdateWatcherNotify(activityId, watcherInfo));
    }

    public List<ActivityWatcherInfo> getAllWatcherInfoList() {
        return watcherInfoMap.values().stream()
            .map(WatcherInfo::toProto)
            .toList();
    }

    public void setDetail(Object detail) {
        this.detail = JsonUtils.encode(detail);
    }

    public void takeWatcherReward(int watcherId) {
        var watcher = watcherInfoMap.get(watcherId);
        if (watcher == null || watcher.isTakenReward()) {
            return;
        }

        var reward = Optional.of(watcher)
            .map(WatcherInfo::getMetadata)
            .map(ActivityWatcherData::getRewardID)
            .map(id -> GameData.getRewardDataMap().get(id.intValue()));

        if (reward.isEmpty()) {
            return;
        }

        List<GameItem> rewards = new ArrayList<>();
        for (ItemParamData param : reward.get().getRewardItemList()) {
            rewards.add(new GameItem(param.getId(), Math.max(param.getCount(), 1)));
        }

        player.getInventory().addItems(rewards, ActionReason.ActivityWatcher);
        watcher.setTakenReward(true);
        save();
    }

    @Entity
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder(builderMethodName = "of")
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WatcherInfo{
        int watcherId;
        int totalProgress;
        int curProgress;
        boolean isTakenReward;


        public boolean isFinished(){
            return curProgress >= totalProgress;
        }

        public ActivityWatcherData getMetadata() {
            return GameData.getActivityWatcherDataMap().get(watcherId);
        }

        public static WatcherInfo init(ActivityWatcher watcher) {
            val watcherData= watcher.getActivityWatcherData();
            val progress = watcherData!=null ? watcherData.getProgress() : 0;
            return WatcherInfo.of()
                .watcherId(watcher.getWatcherId())
                .totalProgress(progress)
                .isTakenReward(false)
                .build();
        }

        public ActivityWatcherInfo toProto() {
            val proto =  new ActivityWatcherInfo();
            proto.setWatcherId(watcherId);
            proto.setCurProgress(curProgress);
            proto.setTotalProgress(totalProgress);
            proto.setTakenReward(isTakenReward);
            return proto;
        }
    }
}
