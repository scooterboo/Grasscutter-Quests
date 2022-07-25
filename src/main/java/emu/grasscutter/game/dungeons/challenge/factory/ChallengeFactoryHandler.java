package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.scripts.data.BaseGroup;

public interface ChallengeFactoryHandler {
    boolean isThisType(int challengeIndex, int challengeId, int param3, int param4, int param5, int param6, Scene scene, BaseGroup group);
    WorldChallenge build(int challengeIndex, int challengeId, int param3, int param4, int param5, int param6, Scene scene, BaseGroup group);
}
