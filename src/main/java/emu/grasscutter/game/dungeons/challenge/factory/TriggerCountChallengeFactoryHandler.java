package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.enums.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.TriggerGroupTriggerTrigger;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.scripts.data.SceneGroup;

import java.util.List;

import static emu.grasscutter.game.dungeons.challenge.enums.ChallengeType.CHALLENGE_TRIGGER_COUNT;

@ChallengeTypeValue(type = CHALLENGE_TRIGGER_COUNT)
public class TriggerCountChallengeFactoryHandler implements ChallengeFactoryHandler {
    /**
     * Build a new challenge
     * @param params: [unused1, triggerTag, triggerCount]
     * AttachChildChallenge with 100, 1001, 65, { 2,998,2 }
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            header,
            List.of(params.get(2)), // parameters
            List.of(new TriggerGroupTriggerTrigger(1, params.get(2), params.get(1))),
            scoreInfo
        );
    }
}
