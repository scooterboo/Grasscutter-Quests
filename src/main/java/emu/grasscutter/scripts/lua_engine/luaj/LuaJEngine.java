package emu.grasscutter.scripts.lua_engine.luaj;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.constants.IntValueEnum;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.ScriptArgs;
import emu.grasscutter.scripts.lua_engine.*;
import emu.grasscutter.utils.FileUtils;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuajContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class LuaJEngine implements LuaEngine {
    private final ScriptEngineManager manager;
    private final ScriptEngineFactory factory;
    private final LuajContext context;

    @Getter private final ScriptEngine engine;
    @Getter(onMethod = @__(@Override))
    private final LuaJSerializer serializer;

    public LuaJEngine() {
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("luaj");
        factory = getEngine().getFactory();

        serializer = new LuaJSerializer();
        context = (LuajContext) engine.getContext();

        // Set engine to replace require as a temporary fix to missing scripts
        context.globals.finder = new ResourceFinder() {
            @Override
            public InputStream findResource(String filename) {
                val targetFile = FileUtils.getScriptPath("Common/" + filename).toFile();
                if (targetFile.exists()) {
                    try {
                        return new FileInputStream(targetFile);
                    } catch (FileNotFoundException e) {
                        Grasscutter.getLogger().error("[LuaJ] exception while reading file {}:", filename, e);
                    }
                } else {
                    Grasscutter.getLogger().warn("trying to load non existent lua file {}", filename);
                }
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public boolean useRawParamString() {
                return true;
            }
        };
    }

    @Override
    public <T extends Enum<T>> boolean addGlobalEnumByOrdinal(String name, T[] enumArray) {
        LuaTable table = new LuaTable();
        Arrays.stream(enumArray).forEach(e -> {
            table.set(e.name(), e.ordinal());
            table.set(e.name().toUpperCase(), e.ordinal());
        });
        context.globals.set(name, table);
        return true;
    }

    @Override
    public <T extends Enum<T> & IntValueEnum> boolean addGlobalEnumByIntValue(String name, T[] enumArray) {
        LuaTable table = new LuaTable();
        Arrays.stream(enumArray).forEach(e -> {
            table.set(e.name(), e.getValue());
            table.set(e.name().toUpperCase(), e.getValue());
        });
        context.globals.set(name, table);
        return true;
    }

    @Override
    public boolean addGlobalStaticClass(String name, Class<?> staticClass) {
        try {
            context.globals.set(name, CoerceJavaToLua.coerce(staticClass.getConstructor().newInstance()));
            return true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            Grasscutter.getLogger().error("Failed to add static class to lua engine: " + name, e);
        }
        return false;
    }

    @Override
    public boolean addObject(String name, Object object) {
        context.globals.set(name, CoerceJavaToLua.coerce(object));
        return true;
    }

    @Nullable
    @Override
    public LuaScript getScript(String scriptName, ScriptType scriptType) {
        final Path scriptPath = FileUtils.getScriptPath(scriptName);
        if (!Files.exists(scriptPath)) return null;

        try {
            return new LuaJScript(this, scriptPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ControllerLuaContext getControllerLuaContext(EntityGadget gadget) {
        return new ControllerLuaJContext(this, gadget);
    }

    @Override
    public GroupEventLuaContext getGroupEventLuaContext(SceneGroup sceneGroupInstance, ScriptArgs args, SceneScriptManager scriptManager) {
        return new GroupEventLuaJContext(this, sceneGroupInstance, args, scriptManager);
    }

    @Override
    public emu.grasscutter.scripts.lua_engine.LuaTable getTable(Object table) {
        if (table instanceof LuaTable)
            return new LuaJTable(this, (LuaTable) table);
        throw new IllegalArgumentException("Table must be a LuaTable");
    }

    @Override
    public emu.grasscutter.scripts.lua_engine.LuaTable createTable() {
        return new LuaJTable(this, new LuaTable());
    }
}
