package emu.grasscutter.scripts.lua_engine.mock_results;

import emu.grasscutter.scripts.lua_engine.LuaValue;

public class BooleanLuaValue implements LuaValue {
    public static final BooleanLuaValue TRUE = new BooleanLuaValue(true);
    public static final BooleanLuaValue FALSE = new BooleanLuaValue(false);
    private final boolean value;
    public BooleanLuaValue(boolean value){
        this.value = value;
    }
    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean isInteger() {
        return false;
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
        return value;
    }

    @Override
    public int asInteger() {
        return value ? 1 : 0;
    }

    @Override
    public long asLong() {
        return value ? 1 : 0;
    }

    @Override
    public double asDouble() {
        return value ? 1 : 0;
    }

    @Override
    public float asFloat() {
        return value ? 1 : 0;
    }

    @Override
    public String asString() {
        return value ? "true" : "false";
    }

    @Override
    public <T> T asObject(Class<T> type) {
        return null;
    }
}
