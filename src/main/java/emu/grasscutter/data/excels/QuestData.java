package emu.grasscutter.data.excels;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.quest.enums.LogicType;
import emu.grasscutter.game.quest.enums.QuestTrigger;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ResourceType(name = "QuestExcelConfigData.json")
@Getter
@ToString
public class QuestData extends GameResource {
    private int subId;
    private int mainId;
    private int order;
    private long descTextMapHash;
    //showType
    private boolean isMpBlock;

    private Guide guide;

    //ResourceLoader not happy if you remove getId() ~~
    public int getId() {
        return subId;
    }
    //Added getSubId() for clarity
    public int getSubId() {return subId;}

    public int getMainId() {
        return mainId;
    }

    public int getOrder() {
        return order;
    }

    public long getDescTextMapHash() {
        return descTextMapHash;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class QuestExecParam {
        @SerializedName("_type")
        QuestTrigger type;
        @SerializedName("_param")
        String[] param;
        @SerializedName("_count")
        String count;
    }

    @Data
    public static class QuestCondition {
        @SerializedName(value = "_type", alternate = "type")
        private QuestTrigger type;
        @SerializedName(value = "_param", alternate = "param")
        private int[] param;
        @SerializedName(value = "_param_str", alternate = "param_str")
        private String paramStr;
        @SerializedName(value = "_count", alternate = "count")
        private int count;

    }

    @Data
    public static class Guide {
        private String type;
        private List<String> param;
        private int guideScene;
    }
}
