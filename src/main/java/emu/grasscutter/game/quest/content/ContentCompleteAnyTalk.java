package emu.grasscutter.game.quest.content;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

import java.util.Arrays;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_COMPLETE_ANY_TALK;

@QuestValueContent(QUEST_CONTENT_COMPLETE_ANY_TALK)
public class ContentCompleteAnyTalk extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        if(condition.getParamString() == null) {
            QuestSystem.getLogger().error("Quest {} has no param string for QUEST_CONTENT_COMPLETE_ANY_TALK!", questData.getSubId());
            return 0;
        }
        val conditionTalk = Arrays.stream(condition.getParamString().split(","))
            .mapToInt(Integer::parseInt)
            .toArray();
        return Arrays.stream(conditionTalk).anyMatch(talkId -> {
            val checkMainQuest = quest.getOwner().getQuestManager().getMainQuestByTalkId(talkId);
            if (checkMainQuest == null || checkMainQuest.getParentQuestId() != questData.getMainId()) {
                return false;
            }
            val talkData = checkMainQuest.getTalks().get(talkId);
            return talkData != null;
        }) ? 1 : 0;
    }
    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type,
                           String paramStr, int... params) {
        if(condition.getType() != type){
            return false;
        }
        if(condition.getParamString() == null) {
            QuestSystem.getLogger().error("Quest {} has no param string for QUEST_CONTENT_COMPLETE_ANY_TALK!", questData.getSubId());
            return false;
        }
        val talkId = params[0];
        val conditionTalk = Arrays.stream(condition.getParamString().split(","))
            .mapToInt(Integer::parseInt)
            .toArray();
        return Arrays.stream(conditionTalk).anyMatch(tids -> tids == talkId);
    }
}
