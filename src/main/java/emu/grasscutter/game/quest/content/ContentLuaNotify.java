package emu.grasscutter.game.quest.content;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_LUA_NOTIFY;

@QuestValueContent(QUEST_CONTENT_LUA_NOTIFY)
public class ContentLuaNotify extends BaseContent {

    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type, String paramStr, int... params) {
        if (condition.getType() != type) {
            return false;
        }
        if (condition.getParamString() == null) {
            QuestSystem.getLogger().error("Quest {} has no param string for QUEST_CONTENT_LUA_NOTIFY!", questData.getSubId());
            return false;
        }
        return condition.getParamString().equals(paramStr);
    }
}
