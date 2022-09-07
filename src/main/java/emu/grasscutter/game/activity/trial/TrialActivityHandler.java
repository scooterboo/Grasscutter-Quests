package emu.grasscutter.game.activity.trial;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.net.proto.*;
import emu.grasscutter.net.proto.ActivityInfoOuterClass.ActivityInfo;
import emu.grasscutter.net.proto.TrialAvatarActivityDetailInfoOuterClass.TrialAvatarActivityDetailInfo;
import emu.grasscutter.net.proto.TrialAvatarActivityRewardDetailInfoOuterClass.TrialAvatarActivityRewardDetailInfo;

import java.util.stream.Collectors;

@GameActivity(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR)
public class TrialActivityHandler extends ActivityHandler {

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo.Builder activityInfo) {
        var builder = TrialAvatarActivityDetailInfo.newBuilder()
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480361)
                .setTrialAvatarIndexId(36))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480202)
                .setTrialAvatarIndexId(26))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480313)
                .setTrialAvatarIndexId(25))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480474)
                .setTrialAvatarIndexId(45))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480021)
                .setTrialAvatarIndexId(1))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480022)
                .setTrialAvatarIndexId(2))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480023)
                .setTrialAvatarIndexId(3))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480024)
                .setTrialAvatarIndexId(4))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480031)
                .setTrialAvatarIndexId(5))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480032)
                .setTrialAvatarIndexId(6))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480033)
                .setTrialAvatarIndexId(7))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480034)
                .setTrialAvatarIndexId(8))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480041)
                .setTrialAvatarIndexId(9))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480042)
                .setTrialAvatarIndexId(10))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480043)
                .setTrialAvatarIndexId(11))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480044)
                .setTrialAvatarIndexId(12))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480051)
                .setTrialAvatarIndexId(13))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480052)
                .setTrialAvatarIndexId(14))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(true)
                .setReceivedReward(false)
                .setRewardId(480053)
                .setTrialAvatarIndexId(15))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(true)
                .setReceivedReward(true)
                .setRewardId(480054)
                .setTrialAvatarIndexId(16))
            .addRewardInfoList(TrialAvatarActivityRewardDetailInfo.newBuilder()
                .setPassedDungeon(false)
                .setReceivedReward(false)
                .setRewardId(480021)
                .setTrialAvatarIndexId(50));
        activityInfo.setTrialAvatarInfo(builder);
    }
}
