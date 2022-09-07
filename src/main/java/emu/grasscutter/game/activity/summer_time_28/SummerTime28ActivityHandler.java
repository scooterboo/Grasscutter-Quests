package emu.grasscutter.game.activity.summer_time_28;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.net.proto.*;
import emu.grasscutter.net.proto.ActivityInfoOuterClass.ActivityInfo;
import emu.grasscutter.net.proto.AsterActivityDetailInfoOuterClass.AsterActivityDetailInfo;
import emu.grasscutter.net.proto.SummerTimeV2DetailInfoOuterClass.SummerTimeV2DetailInfo;
import emu.grasscutter.net.proto.Unk2800CGODFDDALAG.Unk2800_CGODFDDALAG;
import emu.grasscutter.net.proto.Unk2800CGPNLBNMPCM.Unk2800_CGPNLBNMPCM;
import emu.grasscutter.net.proto.Unk2800PHPHMILPOLC.Unk2800_PHPHMILPOLC;
import emu.grasscutter.net.proto.Unk2800FDLKPKFOIIK.Unk2800_FDLKPKFOIIK;

import java.util.stream.Collectors;

@GameActivity(ActivityType.NEW_ACTIVITY_SUMMER_TIME_2_8)
public class SummerTime28ActivityHandler extends ActivityHandler {

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo.Builder activityInfo) {
        var builder = SummerTimeV2DetailInfo.newBuilder()
            .setIsContentClosed(false)
            .setUnk2800ELHBCNPKOJG(12)
            .setUnk2800HDEFJKGDNEH(12);

        for(int i=1; i<=6; i++ ){
            builder.addSummertimeChallangeScores(Unk2800_CGPNLBNMPCM.newBuilder()
                .setIsOpen(true)
                .setOpenTime(activityInfo.getBeginTime())
                .setStageId(i)
                .setBestScore(i*10));
        }
        for(int i=1; i<=4; i++ ){
            builder.addUnk2800PNBLCPIBKPO(Unk2800_CGODFDDALAG.newBuilder()
                .setIsOpen(true)
                .setOpenTime(activityInfo.getBeginTime())
                .setStageId(i)
                .setUnk2800GCPNBJIJEDA(true));
        }

        activityInfo.setSelectedAvatarRewardId(3047001)
            .putActivityCoinMap(141, 100)
            .setSummerTimeV2Info(builder)
            .setUnk2700NONJFHAIFLA(true);

        // enable basic mode for early story access, still needs quest series 2002 finished this will normally be requested via Unk2700_BMDBBHFJMPF
        activityInfo.setUnk2700EDKLLHBEEGE(true);


        for(int i=2014022; i<=2014025; i++ ){
            activityInfo.addExpireCondList(i);
        }

        for(int i=2014001; i<=2014021; i++ ){
            activityInfo.addMeetCondList(i);
        }
        for(int i=2014026; i<=2014027; i++ ){
            activityInfo.addMeetCondList(i);
        }

        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014011)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014010)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014009)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014016)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_START));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014015)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014004)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_START));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014014)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014003)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_START));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014012)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014001)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014013)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014002)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014017)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
        activityInfo.addUnk2800KOMIPKKKOBE(Unk2800_PHPHMILPOLC.newBuilder()
            .setUnk2800CLOCMPFBGMD(2014008)
            .setState(Unk2800_FDLKPKFOIIK.Unk2800_FDLKPKFOIIK_Unk2800_FDPBDHDHAKO));
    }
}
