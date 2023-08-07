package emu.grasscutter.game.quest.content;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

@QuestValueContent(QuestContent.QUEST_CONTENT_TIME_VAR_PASS_DAY)
public class ContentTimeVarPassDay extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        return checkDaysSinceTimeVarCompleted(quest, condition) ? 1 : 0;
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                              String paramStr, int... params) {
        return checkDaysSinceTimeVarCompleted(quest, condition) ? 1 : 0;
    }

    private boolean checkDaysSinceTimeVarCompleted(GameQuest quest, QuestContentCondition condition) {
        if (condition.getParamString() == null) {
            QuestSystem.getLogger().error("Quest {} has no param string for QUEST_CONTENT_TIME_VAR_PASS_DAY!", quest.getSubQuestId());
            return false;
        }
        val mainQuestId = condition.getParam()[0];
        val timeVarIndex = condition.getParam()[1];
        val minDays = Integer.parseInt(condition.getParamString());

        val mainQuest = quest.getOwner().getQuestManager().getMainQuestById(mainQuestId);

        if (mainQuest == null) {
            return false;
        }

        val daysSinceTimeVar = mainQuest.getDaysSinceTimeVar(timeVarIndex);
        if (daysSinceTimeVar == -1) {
            return false;
        }

        return daysSinceTimeVar >= minDays;
    }
}
