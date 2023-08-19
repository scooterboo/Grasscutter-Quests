package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@ResourceType(name = "BlossomGroupsExcelConfigData.json")
@EqualsAndHashCode(callSuper = false)
@Data
public class BlossomGroupsData extends GameResource {
    @Getter(onMethod = @__(@Override))
    private int id;
    private int cityId;
    private int sectionId;
    private List<Integer> refreshTypeVec;
    private List<Integer> newGroupVec;
    private List<Integer> decorateGroupVec;
    private List<Integer> nextCampIdVec;
    private boolean isSafe;
    private boolean isInitialRefresh;
    private int finishProgress;
    private int limitLevel;
    private int fightRadius;
    private int remindRadius;

    public int getNewGroupId(){
        return this.newGroupVec.stream().findAny().orElse(0);
    }

    public int getNextCampId(){
        return this.nextCampIdVec.stream().findAny().orElse(0);
    }

    public int getDecorateGroupId() {return this.decorateGroupVec.stream().findAny().orElse(0);}
}
