package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_UNLOCK_TRANS_POINT;

@QuestValueContent(QUEST_CONTENT_UNLOCK_TRANS_POINT)
public class ContentUnlockTransPoint extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val sceneId = condition.getParam()[0];
        val scenePointId = condition.getParam()[1];
        val scenePoints = quest.getOwner().getUnlockedScenePoints().get(sceneId);
        return scenePoints != null && scenePoints.contains(scenePointId) ? 1 : 0;
    }

    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type,
                           String paramStr, int... params) {
        if(condition.getType() != type){
            return false;
        }
        val sceneId = condition.getParam()[0];
        val scenePointId = condition.getParam()[1];
        return params[0] == sceneId && params[1] == scenePointId;
    }
}
