package emu.grasscutter.game.managers.blossom.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public enum BlossomItemLimitType {
    ITEM_LIMIT_NONE(0),
    ITEM_LIMIT_ACTIVITY_DRAGON_SPINE_DROP(87),
    ITEM_LIMIT_ACTIVITY_SUMMER_TIME_REWARD(1601);

    private final int value;
    private static final Int2ObjectMap<BlossomItemLimitType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, BlossomItemLimitType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }

    BlossomItemLimitType(int value) {
        this.value = value;
    }

    public static BlossomItemLimitType getTypeByValue(int value) {
        return map.getOrDefault(value, ITEM_LIMIT_NONE);
    }

    public static BlossomItemLimitType getTypeByName(String name) {
        return stringMap.getOrDefault(name, ITEM_LIMIT_NONE);
    }
}
