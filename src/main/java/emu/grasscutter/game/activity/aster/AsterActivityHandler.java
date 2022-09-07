package emu.grasscutter.game.activity.aster;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.net.proto.*;
import emu.grasscutter.net.proto.ActivityInfoOuterClass.ActivityInfo;
import emu.grasscutter.net.proto.AsterActivityDetailInfoOuterClass.AsterActivityDetailInfo;
import emu.grasscutter.net.proto.AsterLittleDetailInfoOuterClass.AsterLittleDetailInfo;
import emu.grasscutter.net.proto.AsterMidDetailInfoOuterClass.AsterMidDetailInfo;
import emu.grasscutter.net.proto.AsterLargeDetailInfoOuterClass.AsterLargeDetailInfo;
import emu.grasscutter.net.proto.AsterProgressDetailInfoOuterClass.AsterProgressDetailInfo;

import java.util.stream.Collectors;

@GameActivity(ActivityType.NEW_ACTIVITY_ASTER)
public class AsterActivityHandler extends ActivityHandler {

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {
        //var musicGamePlayerData = MusicGamePlayerData.create();

        //playerActivityData.setDetail(musicGamePlayerData);
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo.Builder activityInfo) {
        var builder = AsterActivityDetailInfo.newBuilder()
            .setIsContentClosed(false)
            .setContentCloseTime(activityInfo.getEndTime())
            .setAsterCredit(20)
            .setIsSpecialRewardTaken(false)
            .setAsterToken(30);

        activityInfo.putActivityCoinMap(109, 109);
        activityInfo.putActivityCoinMap(110, 110);

        builder.setAsterLittle(AsterLittleDetailInfo.newBuilder()
            .setIsOpen(true)
            .setStageId(2)
            .setBeginTime(activityInfo.getBeginTime())
            .setStageBeginTime(activityInfo.getBeginTime()+100)
            .setStageState(AsterLittleStageStateOuterClass.AsterLittleStageState.ASTER_LITTLE_STAGE_STATE_FINISHED));

        builder.setAsterLarge(AsterLargeDetailInfo.newBuilder()
            .setBeginTime(activityInfo.getBeginTime())
            .setIsOpen(true));

        var mid = AsterMidDetailInfo.newBuilder()
            .setBeginTime(activityInfo.getBeginTime())
            .setIsOpen(true)
            .setCollectCount(0);


        mid.addCampList(AsterMidCampInfoOuterClass.AsterMidCampInfo.newBuilder()
            .setCampId(1011)
            .setPos(VectorOuterClass.Vector.newBuilder()
                .setX(1534.41f)
                .setY(335.521f)
                .setZ(-2110.276f))
            .build());

        /*mid.addCampList(AsterMidCampInfoOuterClass.AsterMidCampInfo.newBuilder()
            .setCampId(1012)
            .setPos(VectorOuterClass.Vector.newBuilder()
                .setX(731.015f)
                .setY(204.346f)
                .setZ(264.467f))
            .build());
        builder.setAsterMid(mid);*/

        builder.setAsterProgress(AsterProgressDetailInfo.newBuilder()
            .setLastAutoAddTime(activityInfo.getBeginTime())
            .setCount(0));

       /*
        for(int i=1001; i<=1004; i++ ){
            builder.putMinigameInfoMap(i, FleurFairMinigameInfoOuterClass.FleurFairMinigameInfo.newBuilder()
            .setBalloonInfo( FleurFairBalloonInfoOuterClass.FleurFairBalloonInfo.newBuilder()
                .setBestScore(100))
            .setOpenTime(activityInfo.getBeginTime())
            .setIsOpen(true)
            .setMinigameId(i)
            .build());
        }
        for(int i=2001; i<=2004; i++ ){
            builder.putMinigameInfoMap(i, FleurFairMinigameInfoOuterClass.FleurFairMinigameInfo.newBuilder()
            .setFallInfo( FleurFairFallInfoOuterClass.FleurFairFallInfo.newBuilder()
                .setBestScore(100))
            .setOpenTime(activityInfo.getBeginTime())
            .setIsOpen(true)
            .setMinigameId(i)
            .build());
        }
        for(int i=3001; i<=3004; i++ ){
            builder.putMinigameInfoMap(i, FleurFairMinigameInfoOuterClass.FleurFairMinigameInfo.newBuilder()
            .setMusicInfo( FleurFairMusicGameInfoOuterClass.FleurFairMusicGameInfo.newBuilder()
                .putMusicRecordMap(i-3000, FleurFairMusicRecordOuterClass.FleurFairMusicRecord.newBuilder()
                    .setMaxScore(2)
                    .setMaxCombo(3)
                    .setIsUnlock(true)
                    .build()))
            .setOpenTime(activityInfo.getBeginTime())
            .setIsOpen(true)
            .setMinigameId(i)
            .build());
        }


        for(int i=20031; i<=20034; i++ ){
            builder.putDungeonSectionInfoMap(i, FleurFairDungeonSectionInfoOuterClass.FleurFairDungeonSectionInfo.newBuilder()
            .setSectionId(i-20030)
            .setOpenTime(activityInfo.getBeginTime()+i)
            .setIsOpen(true)
            .build());
        }*/

        activityInfo.setAsterInfo(builder);
       /*MusicGamePlayerData musicGamePlayerData = getMusicGamePlayerData(playerActivityData);

        activityInfo.setMusicGameInfo(MusicGameActivityDetailInfoOuterClass.MusicGameActivityDetailInfo.newBuilder()
            .putAllMusicGameRecordMap(
                musicGamePlayerData.getMusicGameRecord().values().stream()
                    .collect(Collectors.toMap(MusicGamePlayerData.MusicGameRecord::getMusicId, MusicGamePlayerData.MusicGameRecord::toProto)))

            .addAllPersonCustomBeatmap(musicGamePlayerData.getPersonalCustomBeatmapRecord().values().stream()
                .map(MusicGamePlayerData.CustomBeatmapRecord::toPersonalBriefProto)
                .map(MusicBriefInfoOuterClass.MusicBriefInfo.Builder::build)
                .toList())

            .addAllPersonCustomBeatmap(musicGamePlayerData.getOthersCustomBeatmapRecord().values().stream()
                .map(MusicGamePlayerData.CustomBeatmapRecord::toOthersBriefProto)
                .map(MusicBriefInfoOuterClass.MusicBriefInfo.Builder::build)
                .toList())
            .build());
            */
            /*MusicGamePlayerData musicGamePlayerData = getMusicGamePlayerData(playerActivityData);

            activityInfo.setMusicGameInfo(MusicGameActivityDetailInfoOuterClass.MusicGameActivityDetailInfo.newBuilder()
                .putAllMusicGameRecordMap(
                    musicGamePlayerData.getMusicGameRecord().values().stream()
                        .collect(Collectors.toMap(MusicGamePlayerData.MusicGameRecord::getMusicId, MusicGamePlayerData.MusicGameRecord::toProto)))

                .addAllPersonCustomBeatmap(musicGamePlayerData.getPersonalCustomBeatmapRecord().values().stream()
                    .map(MusicGamePlayerData.CustomBeatmapRecord::toPersonalBriefProto)
                    .map(MusicBriefInfoOuterClass.MusicBriefInfo.Builder::build)
                    .toList())

                .addAllPersonCustomBeatmap(musicGamePlayerData.getOthersCustomBeatmapRecord().values().stream()
                    .map(MusicGamePlayerData.CustomBeatmapRecord::toOthersBriefProto)
                    .map(MusicBriefInfoOuterClass.MusicBriefInfo.Builder::build)
                    .toList())
                .build());*/
    }

   /* public MusicGamePlayerData getMusicGamePlayerData(PlayerActivityData playerActivityData){
        if(playerActivityData.getDetail() == null || playerActivityData.getDetail().isBlank()){
            onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        return Grasscutter.getGsonFactory().fromJson(playerActivityData.getDetail(),
            MusicGamePlayerData.class);
    }

    public boolean setMusicGameRecord(PlayerActivityData playerActivityData, MusicGamePlayerData.MusicGameRecord newRecord){
        var musicGamePlayerData = getMusicGamePlayerData(playerActivityData);
        var saveRecord = musicGamePlayerData.getMusicGameRecord().get(newRecord.getMusicId());

        saveRecord.setMaxCombo(Math.max(newRecord.getMaxCombo(), saveRecord.getMaxCombo()));
        saveRecord.setMaxScore(Math.max(newRecord.getMaxScore(), saveRecord.getMaxScore()));

        playerActivityData.setDetail(musicGamePlayerData);
        playerActivityData.save();

        return newRecord.getMaxScore() > saveRecord.getMaxScore();
    }
    public void setMusicGameCustomBeatmapRecord(PlayerActivityData playerActivityData, MusicGamePlayerData.CustomBeatmapRecord newRecord){
        var musicGamePlayerData = getMusicGamePlayerData(playerActivityData);
        musicGamePlayerData.getOthersCustomBeatmapRecord().put(newRecord.getMusicShareId(), newRecord);

        playerActivityData.setDetail(musicGamePlayerData);
        playerActivityData.save();
    }

    public void addPersonalBeatmap(PlayerActivityData playerActivityData, MusicGameBeatmap musicGameBeatmap) {
        var musicGamePlayerData = getMusicGamePlayerData(playerActivityData);
        musicGamePlayerData.getPersonalCustomBeatmapRecord().put(musicGameBeatmap.getMusicShareId(),
            MusicGamePlayerData.CustomBeatmapRecord.of()
                .musicShareId(musicGameBeatmap.getMusicShareId())
                .build());

        playerActivityData.setDetail(musicGamePlayerData);
        playerActivityData.save();
    }

    public void removePersonalBeatmap(PlayerActivityData playerActivityData, MusicGameBeatmap musicGameBeatmap) {
        var musicGamePlayerData = getMusicGamePlayerData(playerActivityData);
        musicGamePlayerData.getPersonalCustomBeatmapRecord().remove(musicGameBeatmap.getMusicShareId());

        playerActivityData.setDetail(musicGamePlayerData);
        playerActivityData.save();
    }*/
}
