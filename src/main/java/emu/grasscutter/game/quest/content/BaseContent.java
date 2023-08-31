package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

public abstract class BaseContent {

    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type, String paramStr, int... params){
        return condition.getType() == type && condition.getParam()[0] == params[0];
    }

    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        return 0;
    }

    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                                String paramStr, int... params){
        return currentProgress+1;
    }

    public boolean checkProgress(GameQuest quest, QuestContentCondition condition, int currentProgress){
        val target = condition.getCount() > 0 ? condition.getCount() : 1;
        return currentProgress >= target;
    }
}
