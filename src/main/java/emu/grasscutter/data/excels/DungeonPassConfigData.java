package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;

import emu.grasscutter.game.dungeons.enums.DungeonPassConditionType;
import emu.grasscutter.game.quest.enums.LogicType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@ResourceType(name = "DungeonPassExcelConfigData.json")
@AllArgsConstructor
public class DungeonPassConfigData extends GameResource {

    public static final DungeonPassConfigData EMPTY =
        new DungeonPassConfigData(0, LogicType.LOGIC_OR, List.of(
            new DungeonPassCondition(DungeonPassConditionType.DUNGEON_COND_NONE, new int[]{0})
        ));

    static {
        GameData.getDungeonPassConfigDataMap().put(EMPTY.getId(), EMPTY);
    }

	@Getter private int id;
    @Getter private LogicType logicType;
    @Getter private List<DungeonPassCondition> conds;

    @Data @AllArgsConstructor
    public static class DungeonPassCondition{
        @Getter private DungeonPassConditionType condType;
        @Getter int[] param;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        conds = conds.stream().filter(condition -> condition.getCondType()!=null).toList();
    }
}
