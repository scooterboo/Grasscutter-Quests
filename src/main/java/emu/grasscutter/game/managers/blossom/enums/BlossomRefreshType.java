package emu.grasscutter.game.managers.blossom.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@SuppressWarnings("SpellCheckingInspection")
public enum BlossomRefreshType {
    BLOSSOM_REFRESH_NONE(0, 0),
    BLOSSOM_REFRESH_SCOIN(1, 70360056),
    BLOSSOM_REFRESH_EXP(3, 70360057),
    BLOSSOM_REFRESH_CRYSTAL(4, 70360001),
    BLOSSOM_REFRESH_INFUSED_CRYSTAL(5, 70360001);
//    BLOSSOM_REFRESH_DRAGON_SPINE_A(6, 70360128),
//    BLOSSOM_REFRESH_DRAGON_SPINE_B(7, 70360128);
//    BLOSSOM_ISLAND_SENTRY_TOWER_A(8),
//    BLOSSOM_ISLAND_SENTRY_TOWER_B(9),
//    BLOSSOM_ISLAND_BOMB(10),
//    BLOSSOM_REFRESH_BLITZ_RUSH_A(11),
//    BLOSSOM_REFRESH_BLITZ_RUSH_B(12);

    private final int value;
    private final int gadgetId; // blossom worktop gadget id
    private static final Int2ObjectMap<BlossomRefreshType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, BlossomRefreshType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }

    BlossomRefreshType(int value, int gadgetId) {
        this.value = value;
        this.gadgetId = gadgetId;
    }

    public static BlossomRefreshType getTypeByValue(int value) {
        return map.getOrDefault(value, BLOSSOM_REFRESH_NONE);
    }

    public static BlossomRefreshType getTypeByName(String name) {
        return stringMap.getOrDefault(name, BLOSSOM_REFRESH_NONE);
    }
}
