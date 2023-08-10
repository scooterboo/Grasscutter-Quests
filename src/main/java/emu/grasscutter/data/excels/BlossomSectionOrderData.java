package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ResourceType(name = "BlossomSectionOrderExcelConfigData.json")
@Data
@EqualsAndHashCode(callSuper = false)
public class BlossomSectionOrderData extends GameResource {
    @Getter(onMethod = @__(@Override))
    private int id;
    private int cityId;
    private int sectionId;
    private int order;
}
