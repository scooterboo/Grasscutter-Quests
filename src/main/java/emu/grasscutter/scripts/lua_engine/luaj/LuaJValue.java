package emu.grasscutter.scripts.lua_engine.luaj;

import emu.grasscutter.scripts.lua_engine.LuaEngine;
import emu.grasscutter.scripts.lua_engine.LuaValue;

public class LuaJValue implements LuaValue {
    private final org.luaj.vm2.LuaValue value;
    private final LuaEngine engine;

    LuaJValue(LuaEngine engine, org.luaj.vm2.LuaValue value) {
        this.engine = engine;
        this.value = value;
    }

    @Override
    public boolean isNull() {
        return value.isnil();
    }

    @Override
    public boolean isBoolean() {
        return value.isboolean();
    }

    @Override
    public boolean isInteger() {
        return value.isint();
    }

    @Override
    public boolean isLong() {
        return value.islong();
    }

    @Override
    public boolean isDouble() {
        return value.isnumber();
    }

    @Override
    public boolean isFloat() {
        return value.isnumber();
    }

    @Override
    public boolean isString() {
        return value.isstring();
    }

    @Override
    public boolean isTable() {
        return value.istable();
    }

    @Override
    public boolean asBoolean() {
        return value.toboolean();
    }

    @Override
    public int asInteger() {
        return value.toint();
    }

    @Override
    public long asLong() {
        return value.tolong();
    }

    @Override
    public double asDouble() {
        return value.todouble();
    }

    @Override
    public float asFloat() {
        return value.tofloat();
    }

    @Override
    public String asString() {
        return value.toString();
    }

    @Override
    public <T> T asObject(Class<T> type) {
        if (!value.istable()) {
            return null;
        }

        return engine.getSerializer().toObject(type, value.checktable());
    }
}
