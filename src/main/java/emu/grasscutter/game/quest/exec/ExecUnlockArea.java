package emu.grasscutter.game.quest.exec;

import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;

@QuestValueExec(QuestExec.QUEST_EXEC_UNLOCK_AREA)
public class ExecUnlockArea extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        // Unlock the trans point for the player.
        int sceneId = Integer.parseInt(paramStr[0]);
        int areaId = Integer.parseInt(paramStr[1]);
        quest.getOwner().getProgressManager().unlockSceneArea(sceneId, areaId);

        // Done.
        return true;
    }
}
