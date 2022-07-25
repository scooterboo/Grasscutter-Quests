package emu.grasscutter.game.activity.fleur_fair;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.net.proto.ActivityInfoOuterClass;
import emu.grasscutter.net.proto.MusicBriefInfoOuterClass;
import emu.grasscutter.net.proto.MusicGameActivityDetailInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairActivityDetailInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairChapterInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairMinigameInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairBalloonInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairFallInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairMusicGameInfoOuterClass;
import emu.grasscutter.net.proto.FleurFairMusicRecordOuterClass;
import emu.grasscutter.net.proto.FleurFairDungeonSectionInfoOuterClass;
import emu.grasscutter.server.packet.send.PacketActivityPlayOpenAnimNotify;

import java.util.stream.Collectors;

@GameActivity(ActivityType.NEW_ACTIVITY_FLEUR_FAIR)
public class FleurFairActivityHandler extends ActivityHandler {

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {
        //var musicGamePlayerData = MusicGamePlayerData.create();

        //playerActivityData.setDetail(musicGamePlayerData);
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfoOuterClass.ActivityInfo.Builder activityInfo) {
        var builder = FleurFairActivityDetailInfoOuterClass.FleurFairActivityDetailInfo.newBuilder()
        .setIsContentClosed(false)
        .setContentCloseTime(activityInfo.getEndTime())
        .setIsDungeonUnlocked(true)
        .setDungeonPunishOverTime(10_000)
        .setObtainedToken(118);
        /*activityInfo.putActivityCoinMap(118, 118)
            .putActivityCoinMap(119, 119)
            .putActivityCoinMap(133, 133);*/
        for(int i=100; i<=133; i++){
            activityInfo.putActivityCoinMap(i, i);
        }

        for(int i=1; i<=4; i++ ){
            builder.addChapterInfoList(i-1, FleurFairChapterInfoOuterClass.FleurFairChapterInfo.newBuilder()
            .setChapterId(i)
            .setOpenTime(activityInfo.getBeginTime()+i)
            .build());
        }
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
            builder.putMinigameInfoMap(i-1000, FleurFairMinigameInfoOuterClass.FleurFairMinigameInfo.newBuilder()
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
        }

        activityInfo.setFleurFairInfo(builder);
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
