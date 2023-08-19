package emu.grasscutter.game.managers.blossom.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@SuppressWarnings("SpellCheckingInspection")
public enum BlossomClientShowType {
    BLOSSOM_SHOWTYPE_NONE(0),
    BLOSSOM_SHOWTYPE_CHALLENGE(1),
    BLOSSOM_SHOWTYPE_NPCTALK(2),
    BLOSSOM_SHOWTYPE_GROUPCHALLENGE(3);

    private final int value;
    private static final Int2ObjectMap<BlossomClientShowType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, BlossomClientShowType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }

    BlossomClientShowType(int value) {
        this.value = value;
    }

    public static BlossomClientShowType getTypeByValue(int value) {
        return map.getOrDefault(value, BLOSSOM_SHOWTYPE_NONE);
    }

    public static BlossomClientShowType getTypeByName(String name) {
        return stringMap.getOrDefault(name, BLOSSOM_SHOWTYPE_NONE);
    }
}
