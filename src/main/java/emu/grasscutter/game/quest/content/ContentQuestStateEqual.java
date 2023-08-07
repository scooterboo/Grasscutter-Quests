package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_QUEST_STATE_EQUAL;

@QuestValueContent(QUEST_CONTENT_QUEST_STATE_EQUAL)
public class ContentQuestStateEqual extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val questId = condition.getParam()[0];
        val questState = condition.getParam()[1];
        val checkQuest = quest.getOwner().getQuestManager().getQuestById(questId);
        if (checkQuest == null) {
            return 0;
        }
        return checkQuest.getState().getValue() == questState ? 1 : 0;
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition, String paramStr, int... params) {
        val requiredQuestState = condition.getParam()[1];
        val currentQuestState = params[1];
        return requiredQuestState == currentQuestState ? 1 : 0;
    }
}
