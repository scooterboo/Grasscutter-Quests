package emu.grasscutter.game.quest.exec;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import lombok.val;
import org.anime_game_servers.core.gi.enums.QuestState;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;

@QuestValueExec(QuestExec.QUEST_EXEC_NOTIFY_GROUP_LUA)
public class ExecNotifyGroupLua extends QuestExecHandler {

    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val sceneId = Integer.parseInt(paramStr[0]);
        val groupId = Integer.parseInt(paramStr[1]);

        val scene = quest.getOwner().getScene();
        val scriptManager = scene.getScriptManager();

        if(scene.getId() != sceneId) {
            return false;
        }
        scene.runWhenFinished(() -> {
            val groupInstance = scriptManager.getGroupInstanceById(groupId);

            if(groupInstance==null) {
                QuestSystem.getLogger().warn("notify, no group instance for:\n group: {} \ncondition: {} \nparamStr {}", groupId, condition, paramStr);
            }

            val questState = quest.getState();
            val args = switch (questState) {
                case QUEST_STATE_FINISHED, QUEST_STATE_FAILED ->
                    // TODO there is a single case of a check for param2 == 3 in 133220675,
                    //  but it's not clear what it does, or if its maybe just to make sure its not triggering
                    new ScriptArgs(groupId, EventType.EVENT_QUEST_FINISH, quest.getSubQuestId(), questState == QuestState.QUEST_STATE_FINISHED ? 1 : 0);
                default ->
                    new ScriptArgs(groupId, EventType.EVENT_QUEST_START, quest.getSubQuestId());
            };
            args.setEventSource(quest.getSubQuestId());

            scriptManager.callEvent(args);
        });

        return true;
    }

}
