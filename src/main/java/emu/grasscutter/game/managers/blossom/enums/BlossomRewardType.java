package emu.grasscutter.game.managers.blossom.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public enum BlossomRewardType {
    BLOSSOM_REWARD_TYPE_NONE(0),
    BLOSSOM_REWARD_TYPE_TO_BAG(1);

    private final int value;
    private static final Int2ObjectMap<BlossomRewardType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, BlossomRewardType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }

    BlossomRewardType(int value) {
        this.value = value;
    }

    public static BlossomRewardType getTypeByValue(int value) {
        return map.getOrDefault(value, BLOSSOM_REWARD_TYPE_NONE);
    }

    public static BlossomRewardType getTypeByName(String name) {
        return stringMap.getOrDefault(name, BLOSSOM_REWARD_TYPE_NONE);
    }
}
