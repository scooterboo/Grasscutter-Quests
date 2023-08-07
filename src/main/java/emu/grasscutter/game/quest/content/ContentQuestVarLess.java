package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_QUEST_VAR_LESS;

@QuestValueContent(QUEST_CONTENT_QUEST_VAR_LESS)
public class ContentQuestVarLess extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val questVarIndex = condition.getParam()[0];
        val questVarValueTarget = condition.getParam()[1];
        val questVarValue = quest.getMainQuest().getQuestVars()[questVarIndex];
        return questVarValue < questVarValueTarget ? 1 : 0;
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition, String paramStr, int... params) {
        val questVarValueTarget = condition.getParam()[1];
        val currentValue = params[1];
        return currentValue < questVarValueTarget ? 1 : 0;
    }
}
