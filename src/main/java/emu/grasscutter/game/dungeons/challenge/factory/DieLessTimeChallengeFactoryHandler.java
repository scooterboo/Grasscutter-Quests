package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.enums.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.scripts.data.SceneGroup;

import java.util.List;

import static emu.grasscutter.game.dungeons.challenge.enums.ChallengeType.CHALLENGE_DIE_LESS_IN_TIME;

@ChallengeTypeValue(type = CHALLENGE_DIE_LESS_IN_TIME)
public class DieLessTimeChallengeFactoryHandler extends ChallengeFactoryHandler {
    /**
     * Build a new challenge
     * @param params: [timeLimit, unused1, unused1, unused2]
     * AttachChildChallenge with 506, 110254, 110154, 180, 1, 0, 0
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            header,
            List.of(params.get(0)), // parameters
            buildChallengeTrigger(List.of(new TimeTrigger(1, params.get(0)))),
            scoreInfo
        );
    }
}
