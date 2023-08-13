package emu.grasscutter.game.tower;

import dev.morphia.annotations.Entity;
import emu.grasscutter.net.proto.TowerMonthlyBriefOuterClass;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder(builderMethodName = "of")
public class TowerMonthlyBriefInfo {
    private int towerScheduleId;
    private int bestFloorIndex;
    private int bestLevelIndex;
    private int totalStartCount;

    public static TowerMonthlyBriefInfo create(int scheduleId) {
        return TowerMonthlyBriefInfo.of()
            .towerScheduleId(scheduleId)
            .build();
    }

    public void update(int scheduleId, int bestFloorIndex, int bestLevelIndex, int totalStartCount) {
        this.towerScheduleId = scheduleId;
        this.bestFloorIndex = bestFloorIndex;
        this.bestLevelIndex = bestLevelIndex;
        this.totalStartCount = totalStartCount;
    }

    public TowerMonthlyBriefOuterClass.TowerMonthlyBrief toProto() {
        return TowerMonthlyBriefOuterClass.TowerMonthlyBrief.newBuilder()
            .setTowerScheduleId(this.towerScheduleId)
            .setBestFloorIndex(this.bestFloorIndex)
            .setBestLevelIndex(this.bestLevelIndex)
            .setTotalStarCount(this.totalStartCount)
            .build();
    }
}
