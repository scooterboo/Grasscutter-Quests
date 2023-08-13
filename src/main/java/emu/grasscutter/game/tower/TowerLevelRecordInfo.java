package emu.grasscutter.game.tower;

import dev.morphia.annotations.Entity;
import emu.grasscutter.net.proto.TowerLevelRecordOuterClass;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

@Entity
@Getter
@Builder(builderMethodName = "of")
public class TowerLevelRecordInfo {
    private int levelId;
    private final Set<Integer> satisfiedCondList = new TreeSet<>();
    @Setter
    private boolean receivedFirstPassReward;

    public static TowerLevelRecordInfo create(int levelId) {
        return TowerLevelRecordInfo.of()
            .levelId(levelId)
            .receivedFirstPassReward(false)
            .build();
    }

    public void update(int star) {
        IntStream.rangeClosed(1, star).forEach(this.satisfiedCondList::add);
    }

    public TowerLevelRecordOuterClass.TowerLevelRecord toProto() {
        return TowerLevelRecordOuterClass.TowerLevelRecord.newBuilder()
            .setLevelId(this.levelId)
            .addAllSatisfiedCondList(this.satisfiedCondList)
            .build();
    }
}
