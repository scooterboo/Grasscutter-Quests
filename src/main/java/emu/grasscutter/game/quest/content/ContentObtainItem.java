package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_OBTAIN_ITEM;

@QuestValueContent(QUEST_CONTENT_OBTAIN_ITEM)
public class ContentObtainItem extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val itemId = condition.getParam()[0];
        return quest.getOwner().getInventory().getItemCountById(itemId);
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                              String paramStr, int... params) {
        return params[1];
    }
}
