package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_UNLOCK_AREA;

@QuestValueContent(QUEST_CONTENT_UNLOCK_AREA)
public class ContentUnlockArea extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val sceneId = condition.getParam()[0];
        val areaId = condition.getParam()[1];
        val unlockedAreas = quest.getOwner().getUnlockedSceneAreas(sceneId);
        return unlockedAreas != null && unlockedAreas.contains(areaId) ? 1 : 0;
    }

    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type,
                           String paramStr, int... params) {
        if(condition.getType() != type){
            return false;
        }
        val sceneId = condition.getParam()[0];
        val areaId = condition.getParam()[1];
        return params[0] == sceneId && params[1] == areaId;
    }
}
