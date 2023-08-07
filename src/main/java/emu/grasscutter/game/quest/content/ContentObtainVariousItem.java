package emu.grasscutter.game.quest.content;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

import java.util.Arrays;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_OBTAIN_VARIOUS_ITEM;

@QuestValueContent(QUEST_CONTENT_OBTAIN_VARIOUS_ITEM)
public class ContentObtainVariousItem extends BaseContent {

    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type, String paramStr, int... params) {
        if(condition.getType() != type){
            return false;
        }
        if (condition.getParamString() == null || condition.getParamString().isEmpty()) {
            QuestSystem.getLogger().error("Quest {} has no param string for QUEST_CONTENT_OBTAIN_VARIOUS_ITEM!", questData.getSubId());
            return false;
        }
        val eventItemId = params[0];
        val itemIds = Arrays.stream(condition.getParamString().split(",")).mapToInt(Integer::parseInt).toArray();
        return Arrays.stream(itemIds).anyMatch(id -> id == eventItemId);
    }

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        return getAllItemCount(quest, condition.getParamString());
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                              String paramStr, int... params) {
        return getAllItemCount(quest, condition.getParamString());
    }

    // TODO we probably need to collect count if each item
    private int getAllItemCount(GameQuest quest, String paramString) {
        if (paramString == null || paramString.isEmpty()) {
            return 0;
        }
        int[] count = {0};
        val inventory = quest.getOwner().getInventory();
        Arrays.stream(paramString.split(",")).mapToInt(Integer::parseInt).forEach(id -> {
            val itemCount = inventory.getItemCountById(id);
            count[0] += itemCount;
        });
        return count[0];
    }
}
