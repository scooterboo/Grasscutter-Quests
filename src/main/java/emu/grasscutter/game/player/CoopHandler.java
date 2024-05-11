package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.data.GameData;
import emu.grasscutter.server.packet.send.PacketCoopChapterUpdateNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import messages.coop.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class CoopHandler {
    @Transient private Player player;
    @Getter private Map<Integer, CoopCardEntry> coopCards;
    @Getter @Setter private int curCoopPoint;

    public CoopHandler() {
        this.coopCards = new Int2ObjectOpenHashMap<>();
        this.curCoopPoint = 0;
    }

    public CoopHandler(Player player) {
        this();
        this.player = player;
    }

    public void unlockChapterUpdateNotify(int chapterId) {
        val coopCard = this.coopCards.get(chapterId);

        //find first point (POINT_START)
        val startPointId = GameData.getCoopPointDataMap().values().stream()
                .filter(j -> j.getChapterId() == chapterId && j.getPointPosId() == 1)
                .toList()
                .get(0)
                .getId();

        //save to database
        //set first point to selfConfidence 5
        coopCard.getPoints()
                .get(startPointId)
                .setSelfConfidence(5);
        //set first point to STATE_STARTED
        coopCard.getPoints()
                .get(startPointId)
                .setState(CoopPointState.STATE_STARTED);
        //set card as accepted
        coopCard.setAccepted(true);

        //build proto
        val coopChapter = new CoopChapter();
        coopChapter.setId(chapterId);
        coopChapter.setState(CoopChapterState.STATE_ACCEPT);
        coopChapter.setTotalEndCount(coopCard.getTotalEndCount());
        //coopCgList
        val coopCgList = new ArrayList<CoopCg>();
        GameData.getCoopCGDataMap().values().stream()
                .filter(x -> x.getChapterId() == chapterId)
                .forEach(x -> coopCgList.add(new CoopCg(x.getId())));
        coopChapter.setCoopCgList(coopCgList);
        //coopPointList
        val coopPointList = new ArrayList<CoopPoint>();
        GameData.getCoopPointDataMap().values().stream()
                .filter(x -> x.getChapterId() == chapterId)
                .forEach(x -> coopPointList.add(new CoopPoint(x.getId())));
        //add first point
        coopPointList.get(startPointId).setSelfConfidence(5);
        coopPointList.get(startPointId).setState(CoopPointState.STATE_STARTED);
        coopChapter.setCoopPointList(coopPointList);
        //coopRewardList
        val coopRewardList = new ArrayList<CoopReward>();
        GameData.getCoopRewardDataMap().values().stream()
                .filter(x -> x.getChapterId() == chapterId)
                .forEach(x -> coopRewardList.add(new CoopReward(x.getId(), CoopRewardState.STATE_LOCK)));
        coopChapter.setCoopRewardList(coopRewardList);

        //send packet
        this.player.sendPacket(new PacketCoopChapterUpdateNotify(coopChapter));
    }


    @Entity
    public static class CoopCardEntry {
        @Getter @Setter private Boolean accepted;
        @Getter @Setter private Boolean viewed;
        @Getter private int totalEndCount;
        @Getter @Setter private int finishedEndCount;
        @Getter private Map<Integer, CoopPointEntry> points;
        @Getter private Map<Integer, CoopCGEntry> CGs;
        @Getter private Map<Integer, CoopRewardEntry> Rewards;
        @Getter @Setter private MainCoopData mainCoop;
        @Getter @Setter private int curCoopPoint;

        public CoopCardEntry(int chapterId) {
            this.accepted = false;
            this.viewed = false;
            //CGs
            this.CGs = new HashMap<>();
            GameData.getCoopCGDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .forEach(j -> this.CGs.put(j.getId(), new CoopCGEntry()));
            //points
            val coopPoints = GameData.getCoopPointDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .toList();
            this.totalEndCount = (int) coopPoints.stream()
                    .filter(j -> j.getType().equals("POINT_END"))
                    .count();
            this.points = new HashMap<>();
            coopPoints.forEach(j -> this.points.put(j.getId(), new CoopPointEntry()));
            //Rewards
            this.Rewards = new HashMap<>();
            GameData.getCoopRewardDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .forEach(j -> this.Rewards.put(j.getId(), new CoopRewardEntry()));
            this.finishedEndCount = 0;
            this.mainCoop = new MainCoopData();
        }

        @Entity
        public static class CoopCGEntry {
            private CoopCGEntry() {
            }
        }

        @Entity
        public static class CoopPointEntry {
            @Getter @Setter private int selfConfidence;
            @Getter @Setter private CoopPointState state;

            private CoopPointEntry() {
                this.selfConfidence = 0;
                this.state = CoopPointState.STATE_UNSTARTED;
            }
        }

        @Entity
        public static class CoopRewardEntry {
            private CoopRewardEntry() {
            }
        }

        @Entity
        public static class MainCoopData {
            @Getter @Setter private int id;
            @Getter @Setter private Map<Integer, Integer> normalVarMap;
            @Getter @Setter private List<Integer> savePointIdList;
            @Getter @Setter private Map<Integer, Integer> seenEndingMap;
            @Getter @Setter private int selfConfidence;
            @Getter @Setter private Status status;
            @Getter @Setter private Map<Integer, Integer> tempVarMap;

            private MainCoopData() {
                this.id = 0;
                this.normalVarMap = new HashMap<>();
                this.savePointIdList = new ArrayList<>();
                this.seenEndingMap = new HashMap<>();
                this.selfConfidence = 0;
                this.status = Status.UNRECOGNISED;
                this.tempVarMap = new HashMap<>();
            }

            public MainCoop toProto() {
                var mainCoop = new MainCoop();
                mainCoop.setId(this.id);
                mainCoop.setNormalVarMap(this.normalVarMap);
                mainCoop.setSavePointIdList(this.savePointIdList);
                mainCoop.setSeenEndingMap(this.seenEndingMap);
                mainCoop.setSelfConfidence(this.selfConfidence);
                mainCoop.setStatus(this.status);
                mainCoop.setTempVarMap(this.tempVarMap);
                return mainCoop;
            }

            public void fromProto(MainCoop mainCoop) {
                this.id = mainCoop.getId();
                this.normalVarMap = mainCoop.getNormalVarMap();
                this.savePointIdList = mainCoop.getSavePointIdList();
                this.seenEndingMap = mainCoop.getSeenEndingMap();
                this.selfConfidence = mainCoop.getSelfConfidence();
                this.status = mainCoop.getStatus();
                this.tempVarMap = mainCoop.getTempVarMap();
            }
        }
    }
}
