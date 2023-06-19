package emu.grasscutter.scripts.lua_engine.jnlua;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.ScriptLib;
import emu.grasscutter.scripts.lua_engine.LuaContext;
import emu.grasscutter.scripts.lua_engine.LuaEngine;
import emu.grasscutter.scripts.lua_engine.LuaScript;
import emu.grasscutter.scripts.lua_engine.LuaValue;
import emu.grasscutter.scripts.lua_engine.mock_results.BooleanLuaValue;
import emu.grasscutter.scripts.lua_engine.mock_results.IntLuaValue;
import emu.grasscutter.utils.FileUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.terasology.jnlua.Converter;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.LuaState;
import org.terasology.jnlua.NamedJavaFunction;
import org.terasology.jnlua.script.CompiledLuaScript;
import org.terasology.jnlua.script.LuaBindings;
import org.terasology.jnlua.script.LuaScriptEngine;

import javax.script.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
        val defaultConverter = binding.getLuaState().getConverter();


        binding.getLuaState().setConverter(new Converter() {
            @Override
            public int getTypeDistance(LuaState luaState, int i, Class<?> aClass) {
                return defaultConverter.getTypeDistance(luaState, i, aClass);
            }

            @Override
            public <T> T convertLuaValue(LuaState luaState, int i, Class<T> aClass) {
                return defaultConverter.convertLuaValue(luaState, i, aClass);
            }

            @Override
            public void convertJavaObject(LuaState luaState, Object o) {
                if(o instanceof Map<?,?>){
                    val fields = (Map<?, ?>) o;
                    val first = fields.entrySet().stream().findFirst();
                    if(first.isPresent() && first.get().getKey() instanceof String && first.get().getValue() instanceof Integer){
                        luaState.newTable();
                        for (var entry : fields.entrySet()) {
                            luaState.pushInteger((Integer)entry.getValue());
                            luaState.setField(-2, (String) entry.getKey());
                        }
                        return;
                    }
                } else if (o instanceof ScriptLib){
                    luaState.newTable();
                    val lib = (ScriptLib) o;
                    val methods = lib.getClass().getMethods();
                    Arrays.stream(methods).forEach(m -> {
                            val isStatic =  Modifier.isStatic(m.getModifiers());
                            class TempFunc implements NamedJavaFunction {
                                @Override
                                public String getName() {
                                    return m.getName();
                                }

                                @SneakyThrows
                                @Override
                                public int invoke(LuaState luaState) {
                                    var argSize = luaState.getTop();
                                    List<Object> args = new ArrayList<>();
                                    for (int i = 0; i < argSize; ++i) {
                                        args.add(luaState.checkJavaObject(i + 1, Object.class));
                                    }
                                    try {
                                        Object caller = isStatic ? null : o;
                                        Object ret = m.invoke(caller, args.toArray());
                                        luaState.pushJavaObject(ret);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.out.println("Error on invoking binding function. " + e);
                                        throw e;
                                    }
                                    return 1;
                                }
                            }
                            val func = new TempFunc();
                        luaState.pushJavaFunction(func);
                        luaState.setField(-2, func.getName());
                        }
                    );

                    return;
                }
                defaultConverter.convertJavaObject(luaState, o);
            }
        });


        val scriptString = Files.readString(scriptPath);
        this.compiledScript = (CompiledLuaScript) ((Compilable) scriptEngine).compile(scriptString);

        binding.put("require", (JavaFunction) luaState -> {
            val requriedName = luaState.checkString(1);
            val path = "Common/"+requriedName + ".lua";
            val includePath = FileUtils.getScriptPath(path);
            if (!Files.exists(includePath)) {
                Grasscutter.getLogger().error("Require script not found. {}", path);
                return 1;
            }
            try {
                val script = (CompiledLuaScript) ((Compilable) scriptEngine).compile(Files.newBufferedReader(includePath));
                //val includeScript = Files.readString(includePath);
                script.eval(context);
                //((LuaBindings)context.getBindings(ScriptContext.ENGINE_SCOPE)).getLuaState().load(includeScript, requriedName);
                //((LuaBindings)context.getBindings(ScriptContext.ENGINE_SCOPE)).getLuaState().call(0, 0);
            } catch (IOException | ScriptException e) {
                Grasscutter.getLogger().error("Error on loading require script. {}", path, e);
                 return 2;
            }
            return 0;
        });
    }
    @Override
    public boolean hasMethod(@NotNull String methodName) {
        return binding.containsKey(methodName);
    }

    @Nullable
    @Override
    public LuaValue callMethod(@NotNull String methodName, Object... args) throws ScriptException, NoSuchMethodException {
        val result = ((Invocable)scriptEngine).invokeFunction(methodName, args);
        if(result instanceof Boolean){
            return ((Boolean) result) ? BooleanLuaValue.TRUE : BooleanLuaValue.FALSE;
        } else if(result instanceof Integer){
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
