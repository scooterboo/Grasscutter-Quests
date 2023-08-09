package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DungeonChallengeConfigData;
import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.enums.ChallengeType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.scripts.data.SceneGroup;
import lombok.val;
import org.reflections.Reflections;

import java.util.*;

import static emu.grasscutter.game.dungeons.challenge.enums.ChallengeType.CHALLENGE_NONE;

public class ChallengeFactory {
    private static final Map<ChallengeType, ChallengeFactoryHandler> challengeFactoryHandlers = new HashMap<>();


    static {
        // Use reflection to scan and find the classes that implement the interface
        val reflections = new Reflections(ChallengeFactoryHandler.class.getPackage().getName());

        // Instantiate objects of those classes dynamically
        reflections.getSubTypesOf(ChallengeFactoryHandler.class).forEach(clazz -> {
            try {
                val classInstance = clazz.getDeclaredConstructor().newInstance();
                Arrays.stream(clazz.getAnnotation(ChallengeTypeValue.class).type()).forEach(type ->
                    challengeFactoryHandlers.put(type, classInstance));
            } catch (Exception e) {
                Grasscutter.getLogger().error("Cannot load handler {}", clazz.getSimpleName(), e);
            }
        });
    }

    /**
     * challengeInfo: currentChallengeIndex, currentChallengeId, fatherChallengeIndex
     * */
    public static WorldChallenge getChallenge(ChallengeInfo challengeInfo, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group){
        val challengeType = Optional.ofNullable(GameData.getDungeonChallengeConfigDataMap().get(challengeInfo.challengeId()))
            .map(DungeonChallengeConfigData::getChallengeType)
            .orElse(CHALLENGE_NONE);
        return Optional.ofNullable(challengeFactoryHandlers.get(challengeType))
            .map(handler -> handler.build(challengeType, challengeInfo, params, scoreInfo, scene, group))
            .orElse(null);
    }
}
