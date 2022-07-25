package emu.grasscutter.game.props;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ActivityType {
    NONE(0),
    NEW_ACTIVITY_TRIAL_AVATAR(4),
    NEW_ACTIVITY_ARENA_CHALLENGE(1402),
    NEW_ACTIVITY_ECHO_SHELL(1602),
    NEW_ACTIVITY_MUSIC_GAME(2202),
    NEW_ACTIVITY_SUMMER_TIME_2_8(2801),
    NEW_ACTIVITY_GEAR(2802), // prob
    NEW_ACTIVITY_ISLAND_PARTY(2803),
    NEW_ACTIVITY_ASTER(1101),//tmp
    NEW_ACTIVITY_FLEUR_FAIR(1401), //tmp
    ;

    private final int value;
    private static final Int2ObjectMap<ActivityType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, ActivityType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }

    public static ActivityType getTypeByValue(int value) {
        return map.getOrDefault(value, NONE);
    }

    public static ActivityType getTypeByName(String name) {
        return stringMap.getOrDefault(name, NONE);
    }
}
