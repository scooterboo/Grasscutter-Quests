package emu.grasscutter.data.server;

import lombok.Data;

@Data
public class MonsterDropExcelConfigData {
    private int level;
    private String dropTag;
    private int dropId;
    private int dropMultiplier;
}
