package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ResourceType(name = "TowerFloorExcelConfigData.json")
@Data
@EqualsAndHashCode(callSuper = false)
public class TowerFloorData extends GameResource {
    private int floorId;
    private int floorIndex;
    private int levelGroupId;
    private int overrideMonsterLevel;
    private int teamNum;
    private int floorLevelConfigId;

    @Override
    public int getId() {
        return this.floorId;
    }
}
