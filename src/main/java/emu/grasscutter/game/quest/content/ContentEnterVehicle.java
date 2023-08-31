package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_ENTER_VEHICLE;

@QuestValueContent(QUEST_CONTENT_ENTER_VEHICLE)
public class ContentEnterVehicle extends BaseContent {
    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type,
                           String paramStr, int... params) {
        if(condition.getType() != type){
            return false;
        }
        // one is 0, so probably any vehicle?
        return (condition.getParam().length == 0 || condition.getParam()[0] == params[0] || condition.getParam()[0] == 0);
    }
}
