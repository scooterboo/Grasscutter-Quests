package emu.grasscutter.game.dungeons.challenge.trigger;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.scripts.data.SceneTrigger;

public class TriggerGroupTriggerTrigger extends ChallengeTrigger{
    private final String TRIGGER_TAG;

    public TriggerGroupTriggerTrigger(int paramIndex, int goal, int triggerTag){
        super(paramIndex, goal);
        this.TRIGGER_TAG = String.valueOf(triggerTag);
    }

    @Override
    public void onGroupTrigger(WorldChallenge challenge, SceneTrigger trigger) {
        if(!this.TRIGGER_TAG.equals(trigger.getTag())) return;

        int newScore = getScore().incrementAndGet();
        onBegin(challenge);

        if(newScore >= getGoal().get()) challenge.done();
    }
}
