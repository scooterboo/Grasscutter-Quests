package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.managers.blossom.enums.BlossomRefreshType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ResourceType(name = "BlossomChestExcelConfigData.json")
@Data
@EqualsAndHashCode(callSuper = false)
public class BlossomChestData extends GameResource {
    @Getter(onMethod = @__(@Override))
    private int id;
    private int chestGadgetId;
    private int worldResin;
    private int resin;
    private String refreshType;
    private BlossomRefreshType blossomRefreshType;

    @Override
    public void onLoad() {
        this.blossomRefreshType = BlossomRefreshType.getTypeByName(this.refreshType);
    }
}
