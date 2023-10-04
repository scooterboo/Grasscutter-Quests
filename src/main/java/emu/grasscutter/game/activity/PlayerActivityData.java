package emu.grasscutter.game.activity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import emu.grasscutter.data.GameData;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.net.proto.ActivityWatcherInfoOuterClass;
import emu.grasscutter.server.packet.send.PacketActivityUpdateWatcherNotify;
import emu.grasscutter.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.game_data_models.data.watcher.ActivityWatcherData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity("activities")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
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

    public List<ActivityWatcherInfoOuterClass.ActivityWatcherInfo> getAllWatcherInfoList() {
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

        val reward = Optional.of(watcher)
            .map(WatcherInfo::getMetadata)
            .map(ActivityWatcherData::getRewardId)
            .map(id -> GameData.getRewardDataMap().get(id.intValue()));

        if (reward.isEmpty()) {
            return;
        }

        player.getInventory().addRewardData(reward.get(), ActionReason.ActivityWatcher);
        watcher.setTakenReward(true);
        save();
    }

    @Entity
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder(builderMethodName = "of")
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

        public ActivityWatcherInfoOuterClass.ActivityWatcherInfo toProto() {
            return ActivityWatcherInfoOuterClass.ActivityWatcherInfo.newBuilder()
                .setWatcherId(watcherId)
                .setCurProgress(curProgress)
                .setTotalProgress(totalProgress)
                .setIsTakenReward(isTakenReward)
                .build();
        }
    }
}
