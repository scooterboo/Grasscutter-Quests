package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_ITEM_LESS_THAN;

@QuestValueContent(QUEST_CONTENT_ITEM_LESS_THAN)
public class ContentItemLessThan extends BaseContent {
    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val itemId = condition.getParam()[0];
        val targetCount = condition.getCount();
        val itemCount = quest.getOwner().getInventory().getItemCountById(itemId);
        return itemCount < targetCount ? 1 : 0;
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                              String paramStr, int... params) {
        val targetCount = condition.getCount();
        return params[1] < targetCount  ? 1 : 0;
    }
}
