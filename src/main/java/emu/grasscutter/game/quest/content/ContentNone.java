package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;

@QuestValueContent(QuestContent.QUEST_CONTENT_NONE)
public class ContentNone extends BaseContent {

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                              String paramStr, int... params) {
        return 1;
    }

    @Override
    public boolean checkProgress(GameQuest quest, QuestContentCondition condition, int currentProgress) {
        return true;
    }
}
