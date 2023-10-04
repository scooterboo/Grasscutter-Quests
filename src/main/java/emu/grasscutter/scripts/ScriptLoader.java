package emu.grasscutter.scripts;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Loggers;
import emu.grasscutter.game.dungeons.challenge.enums.ChallengeEventMarkType;
import emu.grasscutter.game.dungeons.challenge.enums.FatherChallengeProperty;
import emu.grasscutter.game.props.ElementType;
import emu.grasscutter.game.props.EntityType;
import emu.grasscutter.game.quest.enums.QuestState;
import emu.grasscutter.scripts.constants.*;
import emu.grasscutter.scripts.constants.temporary.ExhibitionPlayType;
import emu.grasscutter.scripts.constants.temporary.FlowSuiteOperatePolicy;
import emu.grasscutter.scripts.constants.temporary.GalleryProgressScoreType;
import emu.grasscutter.scripts.constants.temporary.GalleryProgressScoreUIType;
import emu.grasscutter.scripts.data.SceneMeta;
import emu.grasscutter.scripts.lua_engine.LuaEngine;
import emu.grasscutter.scripts.lua_engine.LuaScript;
import emu.grasscutter.scripts.lua_engine.ScriptType;
import emu.grasscutter.scripts.lua_engine.jnlua.JNLuaEngine;
import emu.grasscutter.scripts.lua_engine.luaj.LuaJEngine;
import lombok.Getter;
import org.slf4j.Logger;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptLoader {
    @Getter private static LuaEngine luaEngine;
    private static final Logger logger = Loggers.getScriptSystem();
    /**
     * suggest GC to remove it if the memory is less
     */
    private static Map<String, SoftReference<LuaScript>> scriptsCache = new ConcurrentHashMap<>();
    /**
     * sceneId - SceneMeta
     */
    private static Map<Integer, SoftReference<SceneMeta>> sceneMetaCache = new ConcurrentHashMap<>();

    public synchronized static void init() throws Exception {
        if (luaEngine != null) {
            throw new Exception("Script loader already initialized");
        }

        // Create script engine
        if(Grasscutter.getConfig().server.game.useJNLua){
            logger.info("Using JNLua");
            luaEngine = new JNLuaEngine();
        } else {
            logger.info("Using LuaJ");
            luaEngine = new LuaJEngine();
        }

        luaEngine.addGlobalEnumByIntValue("EntityType", EntityType.values());
        luaEngine.addGlobalEnumByIntValue("QuestState", QuestState.values());
        luaEngine.addGlobalEnumByIntValue("ElementType", ElementType.values());

        luaEngine.addGlobalEnumByOrdinal("GroupKillPolicy", GroupKillPolicy.values());
        luaEngine.addGlobalEnumByOrdinal("SealBattleType", SealBattleType.values());
        luaEngine.addGlobalEnumByOrdinal("FatherChallengeProperty", FatherChallengeProperty.values());
        luaEngine.addGlobalEnumByOrdinal("ChallengeEventMarkType", ChallengeEventMarkType.values());
        luaEngine.addGlobalEnumByOrdinal("VisionLevelType", VisionLevelType.values());
        luaEngine.addGlobalEnumByOrdinal("ExhibitionPlayType", ExhibitionPlayType.values());
        luaEngine.addGlobalEnumByOrdinal("FlowSuiteOperatePolicy", FlowSuiteOperatePolicy.values());
        luaEngine.addGlobalEnumByOrdinal("GalleryProgressScoreUIType", GalleryProgressScoreUIType.values());
        luaEngine.addGlobalEnumByOrdinal("GalleryProgressScoreType", GalleryProgressScoreType.values());

        luaEngine.addGlobalStaticClass("EventType", EventType.class);
        luaEngine.addGlobalStaticClass("GadgetState", ScriptGadgetState.class);
        luaEngine.addGlobalStaticClass("RegionShape", ScriptRegionShape.class);
        luaEngine.addGlobalStaticClass("ScriptLib", ScriptLib.class);
    }

    public static <T> Optional<T> tryGet(SoftReference<T> softReference) {
        try {
            return Optional.ofNullable(softReference.get());
        }catch (NullPointerException npe) {
            return Optional.empty();
        }
    }

    public static LuaScript getScript(String path, ScriptType scriptType) {
        var sc = tryGet(scriptsCache.get(path));
        if (sc.isPresent()) {
            return sc.get();
        }

        try {
            var script = luaEngine.getScript(path, scriptType);
            if(script == null) {
                logger.error("Loading script {} failed! - {}", path, "script is null");
                return null;
            }
            scriptsCache.put(path, new SoftReference<>(script));
            return script;
        } catch (Exception e) {
            logger.error("Loading script {} failed! - {}", path, e.getLocalizedMessage());
            return null;
        }
    }

    public static SceneMeta getSceneMeta(int sceneId) {
        return tryGet(sceneMetaCache.get(sceneId)).orElseGet(() -> {
            var instance = SceneMeta.of(sceneId);
            sceneMetaCache.put(sceneId, new SoftReference<>(instance));
            return instance;
        });
    }

}
