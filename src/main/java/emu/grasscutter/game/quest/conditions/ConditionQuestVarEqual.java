package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.game.quest.QuestValueCond;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_QUEST_VAR_EQUAL;

@QuestValueCond(QUEST_COND_QUEST_VAR_EQUAL)
public class ConditionQuestVarEqual extends BaseConditionQuestVar {

    @Override
    protected boolean doCompare(int variable, int cond) {
        return variable == cond;
    }

}
