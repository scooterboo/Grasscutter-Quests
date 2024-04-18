package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_MAIN_COOP_START;

@QuestValueCond(QUEST_COND_MAIN_COOP_START)
public class ConditionMainCoopStart extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val chapterId = condition.getParam()[0];
        val questId = condition.getParam()[1];
        return chapterId == params[0]
                && (questId == 0 || questId == params[1]);
    }

}