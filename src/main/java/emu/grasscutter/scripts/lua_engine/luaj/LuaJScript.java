package emu.grasscutter.scripts.lua_engine.luaj;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.lua_engine.LuaEngine;
import emu.grasscutter.scripts.lua_engine.LuaScript;
import emu.grasscutter.scripts.lua_engine.LuaValue;
import emu.grasscutter.scripts.lua_engine.mock_results.BooleanLuaValue;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class LuaJScript implements LuaScript {
    private final CompiledScript compiledScript;
    private final Bindings binding;
    private final LuaJEngine engine;

    public LuaJScript(LuaJEngine engine, Path scriptPath) throws IOException, ScriptException {
        this.engine = engine;
        this.compiledScript = ((Compilable) engine.getEngine()).compile(Files.newBufferedReader(scriptPath));
        this.binding = engine.getEngine().createBindings();
    }

    @Override
    public boolean hasMethod(@NotNull String methodName) {
        return binding.containsKey(methodName) && ((org.luaj.vm2.LuaValue) binding.get(methodName)).isfunction();
    }

    @Override
    public LuaValue callMethod(@NotNull String methodName, Object... args) {
        val function = (org.luaj.vm2.LuaValue) binding.get(methodName);
        if (function == null || !function.isfunction()) {
            Grasscutter.getLogger().warn("Attempted to call method {} on script {} but it does not exist or is not a function", methodName, this);
            return BooleanLuaValue.FALSE;
        }

        org.luaj.vm2.LuaValue[] luaArgs;

        luaArgs = new org.luaj.vm2.LuaValue[args.length];
        for (int i = 0; i < args.length; i++)
            luaArgs[i] = CoerceJavaToLua.coerce(args[i]);

        return new LuaJValue(engine, function.invoke(luaArgs).arg1());
    }

    @Override
    public void evaluate() throws ScriptException {
        this.compiledScript.eval(this.binding);
    }

    @Override
    public <T> T getGlobalVariable(String name, Class<T> type) {
        return engine.getSerializer().toObject(type, binding.get(name));
    }

    @Override
    public <T> List<T> getGlobalVariableList(String name, Class<T> type) {
        return engine.getSerializer().toList(type, binding.get(name));
    }

    @Override
    public <T> Map<String, T> getGlobalVariableMap(String name, Class<T> type) {
        return engine.getSerializer().toMap(type, binding.get(name));
    }

    @Override
    public LuaEngine getEngine() {
        return engine;
    }
}
