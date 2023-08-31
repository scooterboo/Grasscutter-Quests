package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_WORKTOP_SELECT;

@QuestValueContent(QUEST_CONTENT_WORKTOP_SELECT)
public class ContentWorktopSelect extends BaseContent {
    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type,
                           String paramStr, int... params) {
        return condition.getType() == type && condition.getParam()[0] == params[0] && condition.getParam()[1] == params[1];
    }
}
