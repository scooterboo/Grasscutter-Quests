package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_QUEST_STATE_NOT_EQUAL;
import static org.anime_game_servers.core.gi.enums.QuestState.QUEST_STATE_NONE;

@QuestValueContent(QUEST_CONTENT_QUEST_STATE_NOT_EQUAL)
public class ContentQuestStateNotEqual extends BaseContent {
    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val questId = condition.getParam()[0];
        val questState = condition.getParam()[1];
        val checkQuest = quest.getOwner().getQuestManager().getQuestById(questId);
        val curQuestState = checkQuest != null ? checkQuest.getState() : QUEST_STATE_NONE;
        return curQuestState.getValue() != questState ? 1 : 0;
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition, String paramStr, int... params) {
        val requiredQuestState = condition.getParam()[1];
        val currentQuestState = params[1];
        return requiredQuestState != currentQuestState ? 1 : 0;
    }

}
