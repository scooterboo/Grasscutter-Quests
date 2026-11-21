package emu.grasscutter.data.server;

import lombok.Data;

@Data
public class ChestDropExcelConfigData {
    private int level;
    private String dropTag;
    private int dropId;
    private int dropMultiplier;
    private int type;
    private String category;
}
