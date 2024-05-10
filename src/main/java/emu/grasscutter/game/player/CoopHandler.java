package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import messages.coop.CoopPointState;
import messages.coop.MainCoop;
import messages.coop.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class CoopHandler {
    @Getter private Map<Integer, CoopCardEntry> coopCards;
    @Getter @Setter private int curCoopPoint;

    public CoopHandler() {
        this.coopCards = new Int2ObjectOpenHashMap<>();
        this.curCoopPoint = 0;
    }

    @Entity
    public static class CoopCardEntry {
        @Getter @Setter private Boolean accepted;
        @Getter @Setter private Boolean viewed;
        @Getter private int totalEndCount;
        @Getter @Setter private int finishedEndCount;
        @Getter private Map<Integer, CoopPointEntry> points;
        @Getter @Setter private MainCoopData mainCoop;
        @Getter @Setter private int curCoopPoint;

        public CoopCardEntry(int chapterId) {
            this.accepted = false;
            this.viewed = false;
            val coopPoints = GameData.getCoopPointDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .toList();
            this.totalEndCount = (int) coopPoints.stream()
                    .filter(j -> j.getType().equals("POINT_END"))
                    .count();
            this.points = new HashMap<>();
            coopPoints.forEach(j -> this.points.put(j.getId(), new CoopPointEntry()));
            this.finishedEndCount = 0;
            this.mainCoop = new MainCoopData();
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
