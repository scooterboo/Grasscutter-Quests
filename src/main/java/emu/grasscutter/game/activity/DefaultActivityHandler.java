package emu.grasscutter.game.activity;

import emu.grasscutter.net.proto.ActivityInfoOuterClass;
import org.anime_game_servers.game_data_models.data.activity.ActivityType;

@GameActivity(ActivityType.NEW_ACTIVITY_GENERAL)
public class DefaultActivityHandler extends ActivityHandler{
    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfoOuterClass.ActivityInfo.Builder activityInfo) {

    }

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {

    }
}
