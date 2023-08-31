package emu.grasscutter.game.dungeons.challenge.trigger;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.entity.EntityMonster;

public class KillMonsterTrigger extends ChallengeTrigger{
    /**
     * For challenge type i.e. "KILL_COUNT_FAST", should reset or increment
     * timer after killing one monster
     * */
    private final boolean RESET_TIMER;
    private final int INC_TIMER_COUNT;

    public KillMonsterTrigger(int paramIndex, int goal){
        this(paramIndex, goal, false);
    }

    public KillMonsterTrigger(int paramIndex, int goal, boolean resetTimer){
        this(paramIndex, goal, resetTimer, 0);
    }

    public KillMonsterTrigger(int paramIndex, int goal, int incTimerCount){
        this(paramIndex, goal, true, incTimerCount);
    }

    /**
     * Used when challenge requires player to kill specific (amount of) monster.
     * Goal will be monster's config id if killing only one monster,
     * otherwise will be the number of monsters to kill.
     * Will also reset/increment time limit for specific challenges
     * */
    public KillMonsterTrigger(int paramIndex, int goal, boolean resetTimer, int incTimerCount){
        super(paramIndex, goal);
        this.RESET_TIMER = resetTimer;
        this.INC_TIMER_COUNT = incTimerCount;
    }

    @Override
    public void onMonsterDeath(WorldChallenge challenge, EntityMonster monster) {
        if(monster.getConfigId() == getGoal().get()) { // for challenge killing specific monster
            challenge.done();
            return;
        }

        int newScore = getScore().incrementAndGet();
        onBegin(challenge);

        if(newScore >= getGoal().get()) {
            challenge.done();
            return;
        }

        if (this.RESET_TIMER) challenge.resetTimer(this.INC_TIMER_COUNT);
    }
}
