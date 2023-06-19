package emu.grasscutter.scripts.lua_engine.mock_results;

import emu.grasscutter.scripts.lua_engine.LuaValue;

public class IntLuaValue implements LuaValue {
    public static final IntLuaValue ZERO = new IntLuaValue(0);
    public static final IntLuaValue ONE = new IntLuaValue(1);
    public static final IntLuaValue N_ONE = new IntLuaValue(-1);


    private final int value;

    public IntLuaValue(int value) {
        this.value = value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    @Override
    public boolean isLong() {
        return false;
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public boolean isFloat() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isTable() {
        return false;
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public String asString() {
        return Integer.toString(value);
    }

    @Override
    public <T> T asObject(Class<T> type) {
        return null;
    }
}
