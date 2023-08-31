package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.enums.QuestContent;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_ANY_MANUAL_TRANSPORT;

@QuestValueContent(QUEST_CONTENT_ANY_MANUAL_TRANSPORT)
public class ContentAnyManualTransport extends BaseContent {
    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type,
                           String paramStr, int... params) {
        return type == condition.getType();
    }
}
