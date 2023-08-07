package emu.grasscutter.scripts.lua_engine;

public interface LuaValue {
    boolean isNull();

    boolean isBoolean();

    boolean isInteger();

    boolean isLong();

    boolean isDouble();

    boolean isFloat();

    boolean isString();

    boolean isTable();

    boolean asBoolean();

    int asInteger();

    long asLong();

    double asDouble();

    float asFloat();

    String asString();

    <T> T asObject(Class<T> type);
}
