package emu.grasscutter.scripts;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Loggers;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.MonsterData;
import emu.grasscutter.data.server.Grid;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.entity.gadget.platform.BaseRoute;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestGroupSuite;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.game.world.SceneGroupInstance;
import emu.grasscutter.net.proto.VisionTypeOuterClass;
import emu.grasscutter.scripts.constants.EventType;
import emu.grasscutter.scripts.data.*;
import emu.grasscutter.scripts.lua_engine.LuaValue;
import emu.grasscutter.scripts.lua_engine.mock_results.BooleanLuaValue;
import emu.grasscutter.scripts.lua_engine.service.ScriptMonsterSpawnService;
import emu.grasscutter.scripts.lua_engine.service.ScriptMonsterTideService;
import emu.grasscutter.server.packet.send.PacketGroupSuiteNotify;
import emu.grasscutter.utils.FileUtils;
import emu.grasscutter.utils.GridPosition;
import emu.grasscutter.utils.JsonUtils;
import emu.grasscutter.utils.Position;
import io.netty.util.concurrent.FastThreadLocalThread;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kotlin.Pair;
import lombok.val;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.script.ScriptException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static emu.grasscutter.scripts.constants.EventType.*;
import static emu.grasscutter.scripts.data.SceneTrigger.INF_TRIGGERS;

public class SceneScriptManager {
    private static final Logger logger = Loggers.getScriptSystem();
    private final Scene scene;
    private final Map<String, Integer> variables;
    private SceneMeta meta;
    private boolean isInit;
    /**
     * current triggers controlled by RefreshGroup
     */
    private final Map<Integer, Set<SceneTrigger>> currentTriggers;
    private final Map<String, Set<SceneTrigger>> triggersByGroupScene;
    private final Map<Integer, Set<Pair<String, Integer>>> activeGroupTimers;
    private final Map<String, AtomicInteger> triggerInvocations;
    private final Map<Integer, EntityRegion> regions; // <EntityId-Region>
    private final Map<Integer, SceneGroup> sceneGroups;
    private final Map<Integer, SceneGroupInstance> sceneGroupsInstances;
    private final Map<Integer, SceneGroupInstance> cachedSceneGroupsInstances;
    private ScriptMonsterTideService scriptMonsterTideService;
    private ScriptMonsterSpawnService scriptMonsterSpawnService;
    /**
     * blockid - loaded groupSet
     */
    private final Map<Integer, Set<SceneGroup>> loadedGroupSetPerBlock;
    private static final Int2ObjectMap<List<Grid>> groupGridsCache = new Int2ObjectOpenHashMap<>();
    public static final ExecutorService eventExecutor;
    static {
        eventExecutor = new ThreadPoolExecutor(4, 4,
                60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000),
                FastThreadLocalThread::new, new ThreadPoolExecutor.AbortPolicy());
    }
    public SceneScriptManager(Scene scene) {
        this.scene = scene;
        this.currentTriggers = new ConcurrentHashMap<>();
        this.triggersByGroupScene = new ConcurrentHashMap<>();
        this.activeGroupTimers = new ConcurrentHashMap<>();
        this.triggerInvocations = new ConcurrentHashMap<>();

        this.regions = new ConcurrentHashMap<>();
        this.variables = new ConcurrentHashMap<>();
        this.sceneGroups = new ConcurrentHashMap<>();
        this.sceneGroupsInstances = new ConcurrentHashMap<>();
        this.cachedSceneGroupsInstances = new ConcurrentHashMap<>();
        this.scriptMonsterSpawnService = new ScriptMonsterSpawnService(this);
        this.loadedGroupSetPerBlock = new ConcurrentHashMap<>();

        // TEMPORARY
        if (this.getScene().getId() < 10 && !Grasscutter.getConfig().server.game.enableScriptInBigWorld) {
            return;
        }

        // Create
        this.init();
    }

    public Scene getScene() {
        return scene;
    }

    public SceneConfig getConfig() {
        if (!isInit) {
            return null;
        }
        return meta.getConfig();
    }

    public Map<Integer, SceneBlock> getBlocks() {
        return meta.getBlocks();
    }

    @Nullable
    public Map<String, Integer> getVariables(int group_id) {
        if(getCachedGroupInstanceById(group_id) == null) return Collections.emptyMap();
        return getCachedGroupInstanceById(group_id).getCachedVariables();
    }

    public Set<SceneTrigger> getTriggersByEvent(int eventId) {
        return currentTriggers.computeIfAbsent(eventId, e -> ConcurrentHashMap.newKeySet());
    }
    public int getTriggerCount() {
        return currentTriggers.size();
    }
    public void registerTrigger(List<SceneTrigger> triggers) {
        triggers.forEach(this::registerTrigger);
    }
    public void registerTrigger(SceneTrigger trigger) {
        triggerInvocations.put(trigger.getName(), new AtomicInteger(0));
        getTriggersByEvent(trigger.getEvent()).add(trigger);
        logger.debug("Registered trigger {}", trigger.getName());
    }

    public void deregisterTrigger(List<SceneTrigger> triggers) {
        triggers.forEach(this::deregisterTrigger);
    }
    public void deregisterTrigger(SceneTrigger trigger) {
        getTriggersByEvent(trigger.getEvent()).remove(trigger);
        logger.debug("deregistered trigger {}", trigger.getName());
    }

    public void resetTriggers(int eventId) {
        currentTriggers.put(eventId, ConcurrentHashMap.newKeySet());
    }

    public void resetTriggersForGroupSuite(SceneGroup group, int suiteIndex) {
        logger.debug("reset triggers for group {} suite {}", group.getId(), suiteIndex);
        var suite = group.getSuiteByIndex(suiteIndex);
        if (suite == null) {
            logger.warn("Trying to load null suite Triggers for group {} with suiteindex {}", group.getId(), suiteIndex);
            return;
        }

        var groupSceneTriggers = triggersByGroupScene.get(group.getId()+"_"+suiteIndex);
        if(groupSceneTriggers == null){
            groupSceneTriggers = new HashSet<>();
        }

        if(!groupSceneTriggers.isEmpty()) {
            for (var trigger : groupSceneTriggers) {
                currentTriggers.get(trigger.getEvent()).remove(trigger);
            }
            groupSceneTriggers.clear();
        }

        if (!suite.getSceneTriggers().isEmpty()) {
            groupSceneTriggers.addAll(suite.getSceneTriggers());
            for (var trigger : groupSceneTriggers) {
                registerTrigger(trigger);
                /*this.currentTriggers.computeIfAbsent(trigger.event, k -> ConcurrentHashMap.newKeySet())
                    .add(trigger);*/
            }
        }
        triggersByGroupScene.put(group.getId()+"_"+suiteIndex, groupSceneTriggers);
    }

    public void refreshGroup(int groupId, int suiteIndex, boolean excludePrevSuite) {
        refreshGroup(getGroupInstanceById(groupId));
    }
    public void refreshGroup(SceneGroupInstance groupInstance) {
        if(groupInstance == null || groupInstance.getLuaGroup().getSuites()==null){
            return;
        }
        //for (int i = 1; i<= group.suites.size();i++){
            //refreshGroup(group, i);
            refreshGroup(groupInstance, groupInstance.getActiveSuiteId(), false); //Refresh the last group triggers
        //}
    }
    public int refreshGroup(SceneGroupInstance groupInstance, int suiteIndex, boolean excludePrevSuite) {
        return refreshGroup(groupInstance, suiteIndex, excludePrevSuite, null, false);
    }
    public int refreshGroup(SceneGroupInstance groupInstance, int suiteIndex, boolean excludePrevSuite, boolean dontLoad) {
        return refreshGroup(groupInstance, suiteIndex, excludePrevSuite, null, dontLoad);
    }
    public int refreshGroup(SceneGroupInstance groupInstance, int suiteIndex, boolean excludePrevSuite, List<GameEntity> entitiesAdded) {
        return refreshGroup(groupInstance, suiteIndex, excludePrevSuite, entitiesAdded, false);
    }
    public int refreshGroup(SceneGroupInstance groupInstance, int suiteIndex, boolean excludePrevSuite, List<GameEntity> entitiesAdded, boolean dontLoad) {
        SceneGroup group = groupInstance.getLuaGroup();
        if(suiteIndex == 0) {
            if(excludePrevSuite) {
                suiteIndex = group.findInitSuiteIndex(groupInstance.getActiveSuiteId());
            } else
                suiteIndex = group.findInitSuiteIndex(0);
        }
        if(suiteIndex == 0) return 0;

        var suiteData = group.getSuiteByIndex(suiteIndex);
        if (suiteData == null) {
            logger.warn("Group {} suite {} not found", group.getId(), suiteIndex);
            return 0;
        }

        int prevSuiteIndex = groupInstance.getActiveSuiteId();
        boolean waitForOne = false;
        SceneSuite prevSuiteData = null;
        if(prevSuiteIndex != 0) {
            prevSuiteData = group.getSuiteByIndex(prevSuiteIndex);
            if (prevSuiteData != null) {
                if(prevSuiteData.isBan_refresh() && !suiteData.isBan_refresh()) {
                    waitForOne = true;
                }
            }
        }

        if(waitForOne && (groupInstance.getTargetSuiteId() == 0 || prevSuiteIndex != groupInstance.getTargetSuiteId())) {
            groupInstance.setTargetSuiteId(suiteIndex);
            logger.debug("Group {} suite {} wating one more refresh", group.getId(), suiteIndex);
            return 0;
        }

        groupInstance.setTargetSuiteId(0);

        if(!dontLoad) {
            if(prevSuiteData != null) {
                removeGroupSuite(group, prevSuiteData);
            } //Remove old group suite

            addGroupSuite(groupInstance, suiteData, entitiesAdded);
        }

        //Refesh variables here
        group.getVariables().forEach(variable -> {
            if(!variable.isNo_refresh())
                groupInstance.getCachedVariables().put(variable.getName(), variable.getValue());
        });

        groupInstance.setActiveSuiteId(suiteIndex);
        groupInstance.setLastTimeRefreshed(getScene().getWorld().getGameTime());
        return suiteIndex;
    }

    public boolean refreshGroupSuite(int groupId, int suiteId) {
        var targetGroupInstance = getGroupInstanceById(groupId);
        if (targetGroupInstance == null) {
            getGroupById(groupId); //Load the group, this ensures an instance is created and the if neccesary unloaded, but the suite data is stored
            targetGroupInstance = getGroupInstanceById(groupId);
            suiteId = refreshGroup(targetGroupInstance, suiteId, false, true); //If suiteId is zero, the value of suiteId changes
            logger.debug("trying to regresh group suite {} in an unloaded and uncached group {} in scene {}", suiteId, groupId, getScene().getId());
        } else {
            logger.debug("Refreshing group {} suite {}", groupId, suiteId);
            suiteId = refreshGroup(targetGroupInstance, suiteId, false); //If suiteId is zero, the value of suiteId changes
        }
        scene.broadcastPacket(new PacketGroupSuiteNotify(groupId, suiteId));

        return true;
    }
    public boolean refreshGroupSuite(int groupId, int suiteId, GameQuest quest) {
        val result = refreshGroupSuite(groupId, suiteId);
        if(suiteId != 0 && quest != null) {
            quest.getMainQuest().getQuestGroupSuites().add(QuestGroupSuite.of()
                .scene(getScene().getId())
                .group(groupId)
                .suite(suiteId)
                .build());
        }

        return result;
    }

    public boolean refreshGroupMonster(int groupId) {
        val groupInstance = getGroupInstanceById(groupId);
        if (groupInstance == null) {
            logger.warn("trying to refresh monster group in unloaded and uncached group {} in scene {}", groupId, getScene().getId());
            return false;
        }

        val group = groupInstance.getLuaGroup();
        val monsters = group.getMonsters();

        if (monsters == null){
            logger.warn("trying to refresh monster group without monsters {} in scene {}", groupId, getScene().getId());
            return false;
        }
        val monstersToSpawn = monsters.values().stream()
            .filter(m -> {
                var entity = scene.getEntityByConfigId(m.getConfig_id());
                return (entity == null || entity.getGroupId()!=group.getId());/*&& !groupInstance.getDeadEntities().contains(entity); */ //TODO: Investigate the usage of deadEntities
            })
            .map(mob -> createMonster(group.getId(), group.block_id, mob))
            .toList();//TODO check if it interferes with bigworld or anything else
        this.addEntities(monstersToSpawn);

        return true;
    }
    public EntityRegion getRegionById(int id) {
        return regions.get(id);
    }

    public void registerRegion(EntityRegion region) {
        regions.put(region.getId(), region);
        logger.debug("Registered region {} from group {}", region.getMetaRegion().getConfig_id(), region.getGroupId());
    }
    public void registerRegionInGroupSuite(SceneGroup group, SceneSuite suite) {
        suite.getSceneRegions().stream().map(region -> new EntityRegion(this.getScene(), region))
            .forEach(this::registerRegion);
    }
    public synchronized void deregisterRegion(SceneRegion region) {
        var instance = regions.values().stream()
            .filter(r -> r.getConfigId() == region.getConfig_id())
            .findFirst();
        instance.ifPresent(entityRegion -> regions.remove(entityRegion.getId()));
    }

    public Map<Integer, Set<SceneGroup>> getLoadedGroupSetPerBlock() {
        return loadedGroupSetPerBlock;
    }

    // TODO optimize
    public SceneGroup getGroupById(int groupId) {
        for (SceneBlock block : getBlocks().values()) {
            getScene().loadBlock(block);
            if(block.getGroups() == null){
                logger.warn("Block {} in scene {} has no groups", block.getId(), getScene().getId());
                continue;
            }

            var group = block.getGroups().get(groupId);
            if (group == null) {
                continue;
            }

            if (!this.sceneGroupsInstances.containsKey(groupId)) {
                getScene().onLoadGroup(List.of(group));
                getScene().onRegisterGroups();
            }
            return group;
        }
        return null;
    }

    public SceneGroupInstance getGroupInstanceById(int groupId) {
        return sceneGroupsInstances.getOrDefault(groupId, null);
    }

    public Map<Integer, SceneGroupInstance> getCachedGroupInstances() {
        return cachedSceneGroupsInstances;
    }

    public SceneGroupInstance getCachedGroupInstanceById(int groupId) {
        var instance = cachedSceneGroupsInstances.getOrDefault(groupId, null);
        if(instance == null) {
            instance = DatabaseHelper.loadGroupInstance(groupId, scene.getWorld().getHost());
            if(instance != null) cachedSceneGroupsInstances.put(groupId, instance);
        }

        return instance;
    }

    private static void addEntityGridPosToMap(List<Map<GridPosition, Set<Integer>>> groupPositions, Set<Integer> visionLevels, SceneObject sceneObject, SceneGroup group){
        visionLevels.add(addEntityGridPosToMap(groupPositions, sceneObject, group));
    }
    private static int addEntityGridPosToMap(List<Map<GridPosition, Set<Integer>>> groupPositions, SceneObject sceneObject, SceneGroup group){
        val visionLevel = switch (sceneObject.getType()){
            case GADGET -> Math.max(getGadgetVisionLevel(((SceneGadget)sceneObject).getGadget_id()), sceneObject.getVision_level());
            case REGION -> 0;
            default -> sceneObject.getVision_level();
        };
        addGridPositionToMap(groupPositions.get(visionLevel), group.getId(), visionLevel, sceneObject.getPos());
        return visionLevel;
    }

    private static void addGridPositionToMap(Map<GridPosition, Set<Integer>> map, int group_id, int vision_level, Position position) {
        //Convert position to grid position
        GridPosition gridPos;
        int width = Grasscutter.getConfig().server.game.visionOptions[vision_level].gridWidth;
        gridPos = new GridPosition((int)(position.getX() / width), (int)(position.getZ() / width), width);

        Set<Integer> groups = map.getOrDefault(gridPos, new HashSet<>());
        groups.add(group_id);
        map.put(gridPos, groups);
    }

    private static int getGadgetVisionLevel(int gadget_id) {
        var gadget = GameData.getGadgetDataMap().get(gadget_id);
        if(gadget == null || gadget.getVisionLevel() == null) return 0;

        var visionOptions = Grasscutter.getConfig().server.game.visionOptions;
        for(int i = 0; i < visionOptions.length; i++)
            if(visionOptions[i].name.compareTo(gadget.getVisionLevel()) == 0) {
                return i;
            }

        return 0;
    }

    private void init() {
        var meta = ScriptLoader.getSceneMeta(getScene().getId());
        if (meta == null) {
            return;
        }
        this.meta = meta;

        // TEMP
        this.isInit = true;
    }

    public List<Grid> getGroupGrids() {
        int sceneId = scene.getId();
        if (groupGridsCache.containsKey(sceneId) && groupGridsCache.get(sceneId) != null) {
            logger.debug("Hit cache for scene {}",sceneId);
            return groupGridsCache.get(sceneId);
        } else {
            var path = FileUtils.getCachePath("scene" + sceneId + "_grid.json");
            if (path.toFile().isFile() && !Grasscutter.config.server.game.cacheSceneEntitiesEveryRun) {
                try {
                    var groupGrids = JsonUtils.loadToList(path, Grid.class);
                    groupGridsCache.put(sceneId, groupGrids);
                    if(groupGrids != null) return groupGrids;
                } catch (IOException e) {
                    logger.error("exception during group grid loading: {}", e);
                }
            }

            //otherwise generate the grids
            List<Map<GridPosition, Set<Integer>>> groupPositions = new ArrayList<>();
            for (int i = 0; i < 6; i++) groupPositions.add(new HashMap<>());

            var visionOptions = Grasscutter.config.server.game.visionOptions;
            meta.getBlocks().values().forEach(block -> {
                block.load(sceneId);
                if(block.getGroups() == null){
                    logger.error("block.groups null for block {}", block.getId());
                    return;
                }
                logger.debug("Loading block grid " + block.getId());
                block.getGroups().values().stream().filter(g -> !g.isDynamic_load()).forEach(group -> {
                    group.load(this.scene.getId());

                    //Add all entities here
                    Set<Integer> vision_levels = new HashSet<>();

                    val monsters = group.getMonsters();
                    if (monsters != null) {
                        monsters.values().forEach(m -> addEntityGridPosToMap(groupPositions, vision_levels, m, group));
                    } else {
                        logger.error("group.monsters null for group {}", group.getId());
                    }
                    val gadgets = group.getGadgets();
                    if (gadgets != null) {
                        gadgets.values().forEach(g -> addEntityGridPosToMap(groupPositions, vision_levels, g, group));
                    } else {
                        logger.error("group.gadgets null for group {}", group.getId());
                    }

                    val npcs = group.getNpcs();
                    if (npcs != null) {
                        npcs.values().forEach(n -> addEntityGridPosToMap(groupPositions, n, group));
                    } else {
                        logger.error("group.npcs null for group {}", group.getId());
                    }

                    val regions = group.getRegions();
                    if (regions != null) {
                        regions.values().forEach(r -> addEntityGridPosToMap(groupPositions, r, group));
                    } else {
                        logger.error("group.regions null for group {}", group.getId());
                    }

                    // TODO should we add those to the grid?
                    val garbages = group.getGarbages();
                    if (garbages != null && garbages.getGadgets() != null) {
                        garbages.getGadgets().forEach(g -> addEntityGridPosToMap(groupPositions, g, group));
                    }

                    int max_vision_level = -1;
                    if (!vision_levels.isEmpty()) {
                        for (int vision_level : vision_levels) {
                            if (max_vision_level == -1 || visionOptions[max_vision_level].visionRange < visionOptions[vision_level].visionRange)
                                max_vision_level = vision_level;
                        }
                    }
                    if (max_vision_level == -1) max_vision_level = 0;

                    addGridPositionToMap(groupPositions.get(max_vision_level), group.getId(), max_vision_level, group.getPos());
                });
            });

            var groupGrids = new ArrayList<Grid>();
            for (int i = 0; i < 6; i++) {
                groupGrids.add(new Grid());
                groupGrids.get(i).grid = groupPositions.get(i);
            }
            groupGridsCache.put(scene.getId(), groupGrids);

            try {
                Files.createDirectories(path.getParent());
            } catch (IOException ignored) {}
            try (var file = new FileWriter(path.toFile())) {
                file.write(JsonUtils.encode(groupGrids));
                logger.info("Scene {} saved grid file.", getScene().getId());
            } catch (Exception e) {
                logger.error("Scene {} unable to save grid file.", getScene().getId(), e);
            }
            return groupGrids;
        }
    }

    public boolean isInit() {
        return isInit;
    }

    public void loadBlockFromScript(SceneBlock block) {
        block.load(scene.getId());
    }

    public void loadGroupFromScript(SceneGroup group) {
        group.load(getScene().getId());

        this.sceneGroups.put(group.getId(), group);
        if(this.getCachedGroupInstanceById(group.getId()) != null) {
            this.sceneGroupsInstances.put(group.getId(), this.cachedSceneGroupsInstances.get(group.getId()));
            this.cachedSceneGroupsInstances.get(group.getId()).setCached(false);
            this.cachedSceneGroupsInstances.get(group.getId()).setLuaGroup(group);
        } else {
            var instance = new SceneGroupInstance(group, getScene().getWorld().getHost());
            this.sceneGroupsInstances.put(group.getId(), instance);
            this.cachedSceneGroupsInstances.put(group.getId(), instance);
            instance.save(); //Save the instance
        }

        if (group.getVariables() != null) {
            group.getVariables().forEach(variable -> {
                val variables = this.getVariables(group.getId());
                if(variables != null && !variables.containsKey(variable.getName()))
                    variables.put(variable.getName(), variable.getValue());
            });
        }
    }

    public void unregisterGroup(SceneGroup group) {
        this.sceneGroups.remove(group.getId());
        this.sceneGroupsInstances.values().removeIf(i -> i.getLuaGroup().equals(group));
        this.cachedSceneGroupsInstances.values().stream().filter(i -> Objects.equals(i.getLuaGroup(),group)).forEach(s -> s.setCached(true));
    }

    public void checkRegions() {
        if (this.regions.size() == 0) {
            return;
        }

        for (var region : this.regions.values()) {
            val metaRegion = region.getMetaRegion();
            // currently all condition_ENTER_REGION Events check for avatar, so we have no necessary to add other types of entity

            getScene().getEntities().values().stream()
                .filter(e -> metaRegion.contains(e.getPosition()) && !region.getEntities().contains(e))
                .forEach(region::addEntity);


            region.getEntities().stream()
                .filter(e -> !metaRegion.contains(e.getPosition()))
                .forEach(region::removeEntity);

            // call enter region events for new entities
            region.getNewEntities().forEach(entity -> callRegionEvent(region, EventType.EVENT_ENTER_REGION, entity));
            region.resetNewEntities();

            // call leave region events for left entities
            region.getLeftEntities().forEach(entity -> callRegionEvent(region, EventType.EVENT_LEAVE_REGION, entity));
            region.resetEntityLeave();
        }
    }

    private void callRegionEvent(EntityRegion region, int eventType, GameEntity entity) {
        callEvent(new ScriptArgs(region.getGroupId(), eventType, region.getConfigId())
            .setEventSource(entity.getEntityType().getValue())
            .setSourceEntityId(region.getId())
            .setTargetEntityId(entity.getId())
        );
    }

    public List<EntityGadget> getGadgetsInGroupSuite(SceneGroupInstance groupInstance, SceneSuite suite) {
        var group = groupInstance.getLuaGroup();
        return suite.getSceneGadgets().stream()
            .filter(m -> {
                var entity = scene.getEntityByConfigId(m.getConfig_id());
                return (entity == null || entity.getGroupId()!=group.getId()) && (!m.isOneoff() || !m.isPersistent() || !groupInstance.getDeadEntities().contains(m.getConfig_id()));
            })
            .map(g -> createGadget(group.getId(), group.block_id, g, groupInstance.getCachedGadgetState(g)))
            .peek(g -> groupInstance.cacheGadgetState(g.getMetaGadget(), g.getState()))
            .filter(Objects::nonNull)
            .toList();
    }
    public List<EntityMonster> getMonstersInGroupSuite(SceneGroupInstance groupInstance, SceneSuite suite) {
        var group = groupInstance.getLuaGroup();
        return suite.getSceneMonsters().stream()
            .filter(m -> {
                var entity = scene.getEntityByConfigId(m.getConfig_id());
                return (entity == null || entity.getGroupId()!=group.getId());/*&& !groupInstance.getDeadEntities().contains(entity); */ //TODO: Investigate the usage of deadEntities
            }) //TODO: Add persistent monster cached data
            .map(mob -> createMonster(group.getId(), group.block_id, mob))
            .filter(Objects::nonNull)
            .toList();
    }

    public void addGroupSuite(SceneGroupInstance groupInstance, SceneSuite suite) {
        addGroupSuite(groupInstance, suite, null);
    }
    public void addGroupSuite(SceneGroupInstance groupInstance, SceneSuite suite, List<GameEntity> entities) {
        // we added trigger first
        registerTrigger(suite.getSceneTriggers());

        var group = groupInstance.getLuaGroup();
        var toCreate = new ArrayList<GameEntity>();
        toCreate.addAll(getGadgetsInGroupSuite(groupInstance, suite));
        toCreate.addAll(getMonstersInGroupSuite(groupInstance, suite));
        if(entities != null)
            toCreate.forEach(entities::add);
        else
            addEntities(toCreate);

        registerRegionInGroupSuite(group, suite);
    }
    public void refreshGroupSuite(SceneGroupInstance groupInstance, SceneSuite suite) {
        // we added trigger first
        registerTrigger(suite.getSceneTriggers());

        var group = groupInstance.getLuaGroup();
        var toCreate = new ArrayList<GameEntity>();
        toCreate.addAll(getGadgetsInGroupSuite(groupInstance, suite));
        toCreate.addAll(getMonstersInGroupSuite(groupInstance, suite));
        addEntities(toCreate);

        registerRegionInGroupSuite(group, suite);
    }
    public void removeGroupSuite(SceneGroup group, SceneSuite suite) {
        deregisterTrigger(suite.getSceneTriggers());
        removeMonstersInGroup(group, suite);
        removeGadgetsInGroup(group, suite);

        suite.getSceneRegions().forEach(this::deregisterRegion);
    }
    public void killGroupSuite(SceneGroup group, SceneSuite suite) {
        deregisterTrigger(suite.getSceneTriggers());

        killMonstersInGroup(group, suite);
        killGadgetsInGroup(group, suite);

        suite.getSceneRegions().forEach(this::deregisterRegion);
    }

    public void startMonsterTideInGroup(int challengeIndex, SceneGroup group, Integer[] ordersConfigId, int tideCount, int sceneLimit) {
        this.scriptMonsterTideService =
                new ScriptMonsterTideService(this, challengeIndex, group, tideCount, sceneLimit, ordersConfigId);

    }
    public void unloadCurrentMonsterTide() {
        if (this.getScriptMonsterTideService() == null) {
            return;
        }
        this.getScriptMonsterTideService().unload();
    }
    public void spawnMonstersByConfigId(SceneGroup group, int configId, int delayTime) {
        // TODO delay
        var entity = scene.getEntityByConfigId(configId);
        if(entity!=null && entity.getGroupId() == group.getId()){
            logger.debug("entity already exists failed in group {} with config {}", group.getId(), configId);
            return;
        }
        val groupMonsters = group.getMonsters();
        if(groupMonsters == null){
            logger.warn("monsters in group {} are null, unable to get configId {}", group.getId(), configId);
            return;
        }
        val monster = groupMonsters.get(configId);
        if(monster == null){
            logger.warn("configId {} not found in group {}", configId, group.getId());
            return;
        }
        entity = createMonster(group.getId(), group.block_id, monster);
        if(entity!=null){
            getScene().addEntity(entity);
        } else {
            logger.warn("failed to create entity with group {} and config {}", group.getId(), configId);
        }
    }
    // Events
    public void callEvent(int groupId, int eventType) {
        callEvent(new ScriptArgs(groupId, eventType));
    }
    public void callEvent(@Nonnull ScriptArgs params) {
        /**
         * We use ThreadLocal to trans SceneScriptManager context to ScriptLib, to avoid eval script for every groups' trigger in every scene instances.
         * But when callEvent is called in a ScriptLib func, it may cause NPE because the inner call cleans the ThreadLocal so that outer call could not get it.
         * e.g. CallEvent -> set -> ScriptLib.xxx -> CallEvent -> set -> remove -> NPE -> (remove)
         * So we use thread pool to clean the stack to avoid this new issue.
         */
        eventExecutor.submit(() -> this.realCallEvent(params));
    }

    private void realCallEvent(@Nonnull ScriptArgs params) {
        try {
            int eventType = params.type;
            Set<SceneTrigger> relevantTriggers = this.getTriggersByEvent(eventType).stream()
                .filter(t -> params.getGroupId() == 0 || t.getCurrentGroup().getId() == params.getGroupId())
                .filter(t ->  (t.getSource().isEmpty() || t.getSource().equals(params.getEventSource())))
                .collect(Collectors.toSet());

            for (SceneTrigger trigger : relevantTriggers) {
                handleEventForTrigger(params, trigger);
            }
        } catch (Throwable throwable){
            logger.error("Condition Trigger {} triggered exception", params.type, throwable);
        }
    }

    private boolean handleEventForTrigger(ScriptArgs params, SceneTrigger trigger ){
        logger.debug("checking trigger {} for event {}", trigger.getName(), params.type);
        try {
            if (evaluateTriggerCondition(trigger, params)) {
                callTrigger(trigger, params);
                return true;
            } else {
                logger.debug("Condition Trigger {} returned false", trigger.getCondition());
            }
            //TODO some ret do not bool
            return false;
        }
        catch (Throwable ex){
            logger.error("Condition Trigger {} triggered exception", trigger.getName(), ex);
            return false;
        }
    }

    /**
     * Checks if the trigger has a condition and calls it if it does.
     * @param trigger
     * @param params
     * @return true if there is no condition, otherwise the result of the condition call as boolean
     */
    private boolean evaluateTriggerCondition(SceneTrigger trigger, ScriptArgs params){
        logger.trace("Call Condition Trigger {}, [{},{},{}]", trigger.getCondition(), params.param1, params.source_eid, params.target_eid);
        val condition = trigger.getCondition();
        if(condition == null || condition.isBlank()){
            return true;
        }
        val ret = this.callScriptFunc(trigger.getCondition(), trigger.getCurrentGroup(), params);
        return ret.isBoolean() && ret.asBoolean();
    }

    private void callTrigger(SceneTrigger trigger, ScriptArgs params){
        val action = trigger.getAction();
        LuaValue callResult = BooleanLuaValue.TRUE;
        if(action != null && !action.isBlank()){
            // the SetGroupVariableValueByGroup in tower need the param to record the first stage time
            callResult = this.callScriptFunc(trigger.getAction(), trigger.getCurrentGroup(), params);
        }

        val invocationsCounter = triggerInvocations.get(trigger.getName());
        val invocations = invocationsCounter.incrementAndGet();
        logger.trace("Call Action Trigger {}", trigger.getAction());


        val activeChallenge = scene.getChallenge();
        if(activeChallenge!=null){
            activeChallenge.onGroupTriggerDeath(trigger);
        }

        val triggerData = GameData.getQuestTriggerDataByName(params.getGroupId(), trigger.getName());
        if (triggerData != null && triggerData.getGroupId() == params.getGroupId()) {
            getScene().getPlayers().forEach(p -> {
                p.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_TRIGGER_FIRE,
                    triggerData.getId(), 0);
            });
        }

        if(trigger.getEvent() == EVENT_TIMER_EVENT){
            cancelGroupTimerEvent(trigger.getCurrentGroup().getId(), trigger.getSource());
        }
        // always deregister on error, otherwise only if the count is reached
        if(callResult.isBoolean() && !callResult.asBoolean() || callResult.isInteger() && callResult.asInteger()!=0
        || trigger.getTrigger_count() > INF_TRIGGERS && invocations >= trigger.getTrigger_count()) {
            deregisterTrigger(trigger);
        }
    }

    private LuaValue callScriptFunc(@Nonnull String funcName, SceneGroup group, ScriptArgs params) {
        val script = group.getScript();
        if(script==null){
            logger.warn("callScriptFunc script is null");
            return BooleanLuaValue.FALSE;
        }

        if(funcName.isEmpty()){
            logger.warn("callScriptFunc funcName is empty");
            return BooleanLuaValue.FALSE;
        }
        if(!script.hasMethod(funcName)){
            logger.warn("callScriptFunc script has no method {}",funcName);
            return BooleanLuaValue.FALSE;
        }

        val context = script.getGroupEventLuaContext(group, params, this);
        try{
            return script.callMethod(funcName, context, params);
        } catch (RuntimeException | ScriptException | NoSuchMethodException error){
            logger.error("[LUA] call trigger failed in group {} with {},{}",group.getId(),funcName,params,error);
            return new BooleanLuaValue(false);
        }
    }

    public ScriptMonsterTideService getScriptMonsterTideService() {
        return scriptMonsterTideService;
    }

    public ScriptMonsterSpawnService getScriptMonsterSpawnService() {
        return scriptMonsterSpawnService;
    }

    public EntityGadget createGadget(int groupId, int blockId, SceneGadget g) {
        return createGadget(groupId, blockId, g, g.getState());
    }

    public EntityGadget createGadget(int groupId, int blockId, SceneGadget g, int state) {
        if (g.isOneoff()) {
            var hasEntity = getScene().getEntities().values().stream()
                .filter(e -> e instanceof EntityGadget)
                .filter(e -> e.getGroupId() == g.getGroup().getId())
                .filter(e -> e.getConfigId() == g.getConfig_id())
                .findFirst();
            if (hasEntity.isPresent()) {
                return null;
            }
        }
        EntityGadget entity = new EntityGadget(getScene(), g.getGadget_id(), g.getPos());

        if (entity.getGadgetData() == null) {
            return null;
        }

        entity.setBlockId(blockId);
        entity.setConfigId(g.getConfig_id());
        entity.setGroupId(groupId);
        entity.getRotation().set(g.getRot());
        entity.setState(state);

        entity.setPointType(g.getPoint_type());
        entity.setRouteConfig(BaseRoute.fromSceneGadget(g));
        entity.setMetaGadget(g);
        entity.buildContent();

        return entity;
    }
    public EntityNPC createNPC(SceneNPC npc, int blockId, int suiteId) {
        return new EntityNPC(getScene(), npc, blockId, suiteId);
    }
    public EntityMonster createMonster(int groupId, int blockId, SceneMonster monster) {
        if (monster == null) {
            return null;
        }

        MonsterData data = GameData.getMonsterDataMap().get(monster.getMonster_id());

        if (data == null) {
            return null;
        }

        // Calculate level
        int level = monster.getLevel();

        if (getScene().getDungeonManager() != null) {
            level = getScene().getDungeonManager().getLevelForMonster(monster.getConfig_id());
        } else if (getScene().getWorld().getWorldLevel() > 0) {
            val worldLevelData = GameData.getWorldLevelDataMap().get(getScene().getWorld().getWorldLevel());

            if (worldLevelData != null) {
                level = worldLevelData.getMonsterLevel();
            }
        }

        // Spawn mob
        EntityMonster entity = new EntityMonster(getScene(), data, monster.getPos(), level);
        entity.getRotation().set(monster.getRot());
        entity.setGroupId(groupId);
        entity.setBlockId(blockId);
        entity.setConfigId(monster.getConfig_id());
        entity.setPoseId(monster.getPose_id());
        entity.setMetaMonster(monster);

        this.getScriptMonsterSpawnService()
                .onMonsterCreatedListener.forEach(action -> action.onNotify(entity));

        return entity;
    }

    public void addEntity(GameEntity gameEntity) {
        getScene().addEntity(gameEntity);
    }

    public void meetEntities(List<? extends GameEntity> gameEntity) {
        getScene().addEntities(gameEntity, VisionTypeOuterClass.VisionType.VISION_TYPE_MEET);
    }

    public void addEntities(List<? extends GameEntity> gameEntity) {
        getScene().addEntities(gameEntity);
    }

    public void removeEntities(List<? extends GameEntity> gameEntity) {
        getScene().removeEntities(gameEntity.stream().map(e -> (GameEntity) e).collect(Collectors.toList()), VisionTypeOuterClass.VisionType.VISION_TYPE_REFRESH);
    }

    public RTree<SceneBlock, Geometry> getBlocksIndex() {
        return meta.getSceneBlockIndex();
    }
    public void removeMonstersInGroup(SceneGroup group, SceneSuite suite) {
        var configSet = suite.getSceneMonsters().stream()
                .map(SceneObject::getConfig_id)
                .collect(Collectors.toSet());
        var toRemove = getScene().getEntities().values().stream()
                .filter(e -> e instanceof EntityMonster)
                .filter(e -> e.getGroupId() == group.getId())
                .filter(e -> configSet.contains(e.getConfigId()))
                .toList();

        getScene().removeEntities(toRemove, VisionTypeOuterClass.VisionType.VISION_TYPE_MISS);
    }
    public void removeGadgetsInGroup(SceneGroup group, SceneSuite suite) {
        var configSet = suite.getSceneGadgets().stream()
                .map(SceneObject::getConfig_id)
                .collect(Collectors.toSet());
        var toRemove = getScene().getEntities().values().stream()
                .filter(e -> e instanceof EntityGadget)
                .filter(e -> e.getGroupId() == group.getId())
                .filter(e -> configSet.contains(e.getConfigId()))
                .toList();

        getScene().removeEntities(toRemove, VisionTypeOuterClass.VisionType.VISION_TYPE_MISS);
    }

    public void killMonstersInGroup(SceneGroup group, SceneSuite suite) {
        var configSet = suite.getSceneMonsters().stream()
                .map(SceneObject::getConfig_id)
                .collect(Collectors.toSet());
        var toRemove = getScene().getEntities().values().stream()
                .filter(e -> e instanceof EntityMonster)
                .filter(e -> e.getGroupId() == group.getId())
                .filter(e -> configSet.contains(e.getConfigId()))
                .toList();

        toRemove.forEach(getScene()::killEntity);
    }
    public void killGadgetsInGroup(SceneGroup group, SceneSuite suite) {
        var configSet = suite.getSceneGadgets().stream()
                .map(SceneObject::getConfig_id)
                .collect(Collectors.toSet());
        var toRemove = getScene().getEntities().values().stream()
                .filter(e -> e instanceof EntityGadget)
                .filter(e -> e.getGroupId() == group.getId())
                .filter(e -> configSet.contains(e.getConfigId()))
                .toList();

        toRemove.forEach(getScene()::killEntity);
    }

    public int createGroupTimerEvent(int groupID, String source, double time) {
        //TODO also remove timers when refreshing and test
        var group = getGroupById(groupID);
        if(group == null || group.getTriggers() == null){
            logger.warn("trying to create a timer for unknown group with id {} and source {}", groupID, source);
            return 1;
        }
        logger.info("creating group timer event for group {} with source {} and time {}",
            groupID, source, time);
        for(SceneTrigger trigger : group.getTriggers().values()){
            if(trigger.getEvent() == EVENT_TIMER_EVENT &&trigger.getSource().equals(source)){
                logger.warn("[LUA] Found timer trigger with source {} for group {} : {}",
                    source, groupID, trigger.getName());
                cancelGroupTimerEvent(groupID, source);
                var taskIdentifier = Grasscutter.getGameServer().getScheduler().scheduleDelayedRepeatingTask(() ->
                    callEvent(new ScriptArgs(groupID, EVENT_TIMER_EVENT)
                        .setEventSource(source)), (int)time, (int)time);
                var groupTasks = activeGroupTimers.computeIfAbsent(groupID, k -> new HashSet<>());
                groupTasks.add(new Pair<>(source, taskIdentifier));

            }
        }
        return 0;
    }
    public int cancelGroupTimerEvent(int groupID, String source) {
        //TODO test
        var groupTimers = activeGroupTimers.get(groupID);
        if(groupTimers!=null && !groupTimers.isEmpty()) {
            for (var timer : new HashSet<>(groupTimers)) {
                if (timer.component1().equals(source)) {
                    Grasscutter.getGameServer().getScheduler().cancelTask(timer.component2());
                    groupTimers.remove(timer);
                    return 0;
                }
            }
        }

        logger.warn("trying to cancel a timer that's not active {} {}", groupID, source);
        return 1;
    }

    // todo use killed monsters instead of spawned entites for check?
    public boolean isClearedGroupMonsters(int groupId) {
        val groupInstance = getGroupInstanceById(groupId);
        if (groupInstance == null || groupInstance.getLuaGroup() == null) return false;

        val monsters = groupInstance.getLuaGroup().getMonsters();

        if(monsters == null || monsters.isEmpty()) return true;

        return monsters.values().stream().noneMatch(m -> {
            val entity = scene.getEntityByConfigId(m.getConfig_id());
            return entity != null && entity.getGroupId() == groupId;
        });
    }

    public void onDestroy(){
        activeGroupTimers.forEach((gid,times) -> times.forEach((e)->Grasscutter.getGameServer().getScheduler().cancelTask(e.getSecond())));
        activeGroupTimers.clear();
    }
}
