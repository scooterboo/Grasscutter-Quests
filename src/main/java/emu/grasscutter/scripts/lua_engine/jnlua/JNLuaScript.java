package emu.grasscutter.scripts.lua_engine.jnlua;

import emu.grasscutter.scripts.lua_engine.LuaEngine;
import emu.grasscutter.scripts.lua_engine.LuaScript;
import emu.grasscutter.scripts.lua_engine.LuaValue;
import emu.grasscutter.scripts.lua_engine.mock_results.BooleanLuaValue;
import emu.grasscutter.scripts.lua_engine.mock_results.IntLuaValue;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.terasology.jnlua.script.CompiledLuaScript;
import org.terasology.jnlua.script.LuaBindings;
import org.terasology.jnlua.script.LuaScriptEngine;

import javax.script.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class JNLuaScript implements LuaScript {

    private final CompiledLuaScript compiledScript;
    private final LuaBindings binding;
    private final JNLuaEngine engine;
    private final LuaScriptEngine scriptEngine;
    SimpleScriptContext context = new SimpleScriptContext();

    JNLuaScript(JNLuaEngine engine, Path scriptPath) throws ScriptException, IOException {
        this.engine = engine;
        this.scriptEngine = (LuaScriptEngine) engine.getManager().getEngineByName("Lua");
        context.setBindings(engine.getBindings(), ScriptContext.GLOBAL_SCOPE);
        this.binding = (LuaBindings) scriptEngine.createBindings();
        context.setBindings(binding, ScriptContext.ENGINE_SCOPE);
        val luaState = binding.getLuaState();
        luaState.setConverter(JNLuaConverter.getINSTANCE());

        val requireFunction = JNLuaRequireCommonFunction.INSTANCE;
        binding.put(requireFunction.getName(), requireFunction);
        val scriptString = Files.newBufferedReader(scriptPath);
        this.compiledScript = (CompiledLuaScript) ((Compilable) scriptEngine).compile(scriptString);
    }

    @Override
    public boolean hasMethod(@NotNull String methodName) {
        return binding.containsKey(methodName);
    }

    @Nullable
    @Override
    public LuaValue callMethod(@NotNull String methodName, Object... args) throws ScriptException, NoSuchMethodException {
        val result = ((Invocable) scriptEngine).invokeFunction(methodName, args);
        if (result instanceof Boolean) {
            return ((Boolean) result) ? BooleanLuaValue.TRUE : BooleanLuaValue.FALSE;
        } else if (result instanceof Integer) {
            return new IntLuaValue((Integer) result);
        }
        //TODO
        return null;
    }

    @Override
    public void evaluate() throws ScriptException {
        compiledScript.eval(context);
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
