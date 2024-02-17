package emu.grasscutter.game.activity;

import emu.grasscutter.game.props.ActivityType;
import messages.activity.ActivityInfo;

@GameActivity(ActivityType.NONE)
public class DefaultActivityHandler extends ActivityHandler{
    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {

    }

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {

    }
}
