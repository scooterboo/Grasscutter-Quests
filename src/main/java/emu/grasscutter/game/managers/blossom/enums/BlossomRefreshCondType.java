package emu.grasscutter.game.managers.blossom.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public enum BlossomRefreshCondType {
    BLOSSOM_REFRESH_COND_NONE(0),
    BLOSSOM_REFRESH_COND_PLAYER_LEVEL_EQUAL_GREATER(1),
    BLOSSOM_REFRESH_COND_PLAYER_LEVEL_LESS_THAN(2),
    BLOSSOM_REFRESH_COND_OPEN_STATE(3),
    BLOSSOM_REFRESH_COND_ACTIVITY_COND(5),
    BLOSSOM_REFRESH_COND_SCENE_TAG_ADDED(6);

    private final int value;
    private static final Int2ObjectMap<BlossomRefreshCondType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, BlossomRefreshCondType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }
    BlossomRefreshCondType(int value) {
        this.value = value;
    }

    public static BlossomRefreshCondType getTypeByValue(int value) {
        return map.getOrDefault(value, BLOSSOM_REFRESH_COND_NONE);
    }

    public static BlossomRefreshCondType getTypeByName(String name) {
        return stringMap.getOrDefault(name, BLOSSOM_REFRESH_COND_NONE);
    }
}
