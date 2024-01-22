package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.*;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_STATE_NOT_EQUAL;
import static org.anime_game_servers.core.gi.enums.QuestState.QUEST_STATE_NONE;

@QuestValueCond(QUEST_COND_STATE_NOT_EQUAL)
public class ConditionStateNotEqual extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val questId = condition.getParam()[0];
        val questStateValue = condition.getParam()[1];
        val checkQuest = owner.getQuestManager().getQuestById(questId);
        val curQuestState = checkQuest != null ? checkQuest.getState() : QUEST_STATE_NONE;
        return curQuestState.getValue() != questStateValue;
    }

}
