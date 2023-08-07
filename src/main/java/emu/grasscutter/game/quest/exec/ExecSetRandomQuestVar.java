package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_RANDOM_QUEST_VAR)
public class ExecSetRandomQuestVar extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val varIndex = Integer.parseInt(paramStr[0]);
        val min = Integer.parseInt(paramStr[1]);
        val max = Integer.parseInt(paramStr[2]);
        quest.getMainQuest().setRandomQuestVar(varIndex, min, max);
        return true;
    }
}
