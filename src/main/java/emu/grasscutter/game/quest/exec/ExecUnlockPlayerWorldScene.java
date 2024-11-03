package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.server.packet.send.PacketPlayerWorldSceneInfoListNotify;

@QuestValueExec(QuestExec.QUEST_EXEC_UNLOCK_PLAYER_WORLD_SCENE)
public class ExecUnlockPlayerWorldScene extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        int sceneId = Integer.parseInt(paramStr[0]);

        //set the scene to locked: false
        quest.getOwner().getUnlockedScenes().put(sceneId, false);

        //send a new PlayerWorldSceneInfoListNotify
        quest.getOwner().getSession().send(new PacketPlayerWorldSceneInfoListNotify(quest.getOwner()));
        return true;
    }
}
