package emu.grasscutter.game.world;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameDepot;
import emu.grasscutter.data.binout.SceneNpcBornEntry;
import emu.grasscutter.data.binout.routes.Route;
import emu.grasscutter.data.excels.*;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.dungeons.DungeonManager;
import emu.grasscutter.game.dungeons.settle_listeners.DungeonSettleListener;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.enums.DungeonPassConditionType;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.entity.gadget.GadgetWorktop;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.TeamInfo;
import emu.grasscutter.game.props.*;
import emu.grasscutter.game.quest.QuestGroupSuite;
import emu.grasscutter.game.world.data.TeleportProperties;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.proto.AttackResultOuterClass.AttackResult;
import emu.grasscutter.net.proto.EnterTypeOuterClass;
import emu.grasscutter.net.proto.SelectWorktopOptionReqOuterClass;
import emu.grasscutter.net.proto.VisionTypeOuterClass.VisionType;
import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.constants.EventType;
import emu.grasscutter.scripts.data.SceneBlock;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.SceneInitConfig;
import emu.grasscutter.scripts.data.ScriptArgs;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.KahnsSort;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.game_data_models.data.world.WorldLevelData;
import org.jetbrains.annotations.NotNull;
import org.anime_game_servers.game_data_models.data.scene.SceneData;
import org.anime_game_servers.game_data_models.data.scene.SceneType;

import javax.annotation.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Scene {
    @Getter private final World world;
    @Getter private final SceneData sceneData;
    @Getter private final List<Player> players = new CopyOnWriteArrayList<>();
    @Getter private final Map<Integer, GameEntity> entities = new ConcurrentHashMap<>();
    @Getter private final Map<Integer, GameEntity> weaponEntities = new ConcurrentHashMap<>();
    private final Set<SpawnDataEntry> spawnedEntities = ConcurrentHashMap.newKeySet();
    @Getter private final Set<SpawnDataEntry> deadSpawnedEntities = ConcurrentHashMap.newKeySet();
    private final Set<SceneBlock> loadedBlocks = ConcurrentHashMap.newKeySet();
    @Getter private final Set<SceneGroup> loadedGroups = ConcurrentHashMap.newKeySet();
    private final Set<Integer> replacedGroup = ConcurrentHashMap.newKeySet();
    private final HashSet<Integer> unlockedForces = new HashSet<>();
    private final List<Runnable> afterLoadedCallbacks = new ArrayList<>();
    private final long startWorldTime;
    @Getter @Setter DungeonManager dungeonManager;
    @Getter Int2ObjectMap<Route> sceneRoutes;
    private Set<SpawnDataEntry.GridBlockId> loadedGridBlocks = new HashSet<>();
    @Getter @Setter private boolean dontDestroyWhenEmpty;
    @Getter private final SceneScriptManager scriptManager;
    @Getter @Setter private WorldChallenge challenge;
    @Getter private final List<DungeonSettleListener> dungeonSettleListeners = new ArrayList<>();
    @Getter @Setter private int prevScene; // id of the previous scene
    @Getter @Setter private int prevScenePoint;
    @Getter @Setter private int killedMonsterCount;
    @Getter @Setter private int killChestCount;
    private Set<SceneNpcBornEntry> npcBornEntrySet = ConcurrentHashMap.newKeySet();
    @Getter private boolean finishedLoading = false;
    @Getter private int tickCount = 0;
    @Getter private boolean isPaused = false;

    @Getter private final GameEntity sceneEntity;

    public Scene(World world, SceneData sceneData) {
        this.world = world;
        this.sceneData = sceneData;

        this.prevScene = 3;
        this.sceneRoutes = GameData.getSceneRoutes(getId());

        this.startWorldTime = world.getWorldTime();

        this.scriptManager = new SceneScriptManager(this);

        //Create scene entity
        this.sceneEntity = new EntityScene(this);
    }

    public int getId() {
        return this.sceneData.getId();
    }

    public SceneType getSceneType() {
        return this.sceneData.getType();
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public GameEntity getEntityById(int id) {
        if (id == 0x13800001) return this.sceneEntity;
        else if (id == this.world.getLevelEntityId()) return this.world.getEntity();

        val teamEntityPlayer = this.players.stream().filter(p -> p.getTeamManager().getEntity().getId() == id).findAny();
        if(teamEntityPlayer.isPresent()) return teamEntityPlayer.get().getTeamManager().getEntity();

        Optional<GameEntity> entity = Optional.ofNullable(this.entities.get(id)).or(() -> Optional.ofNullable(this.weaponEntities.get(id)));
        if (entity.isEmpty() && EntityIdType.idFromEntityId(id) == EntityIdType.AVATAR.getId()) {
            entity = this.players.stream().map(p -> p.getTeamManager().getActiveTeam()).flatMap(List::stream)
                .filter(entityAvatar -> entityAvatar.getId() == id).findFirst().map(GameEntity.class::cast);
        }

        return entity.orElse(null);
    }

    public GameEntity getEntityByConfigId(int configId) {
        return this.entities.values().stream()
            .filter(x -> x.getConfigId() == configId)
            .findFirst()
            .orElse(null);
    }

    public GameEntity getEntityByConfigId(int configId, int groupId) {
        return this.entities.values().stream()
            .filter(x -> x.getConfigId() == configId && x.getGroupId() == groupId)
            .findFirst()
            .orElse(null);
    }

    @Nullable
    public Route getSceneRouteById(int routeId) {
        return this.sceneRoutes.get(routeId);
    }

    public void setPaused(boolean paused) {
        if (this.isPaused != paused) {
            this.isPaused = paused;
            broadcastPacket(new PacketSceneTimeNotify(this));
        }
    }

    public int getSceneTime() {
        return (int) (this.world.getWorldTime() - this.startWorldTime);
    }

    public int getSceneTimeSeconds() {
        return getSceneTime() / 1000;
    }

    public void addDungeonSettleObserver(DungeonSettleListener dungeonSettleListener) {
        this.dungeonSettleListeners.add(dungeonSettleListener);
    }

    public void triggerDungeonEvent(DungeonPassConditionType conditionType, int... params) {
        Optional.ofNullable(this.dungeonManager).ifPresent(m -> m.triggerEvent(conditionType, params));
    }

    private boolean isInScene(GameEntity entity) {
        return this.entities.containsKey(entity.getId());
    }

    public synchronized void addPlayer(Player player) {
        // Check if player already in
        if (this.players.contains(player)) return;

        // Remove player from prev scene
        if (player.getScene() != null) player.getScene().removePlayer(player);

        // Add
        this.players.add(player);
        player.setSceneId(getId());
        player.setScene(this);

        setupPlayerAvatars(player);
    }

    public synchronized void removePlayer(Player player) {
        // Remove from challenge if leaving, challenge has progress check
        Optional.ofNullable(this.challenge).ifPresent(WorldChallenge::fail);

        // Remove player from scene
        this.players.remove(player);
        player.setScene(null);

        // Remove player avatars
        removePlayerAvatars(player);

        // Remove player gadgets
        player.getTeamManager().getGadgets().forEach(this::removeEntity);

        // Deregister scene if not in use
        if (getPlayerCount() <= 0 && !this.dontDestroyWhenEmpty) {
            this.scriptManager.onDestroy();
            this.world.deregisterScene(this);
        }
        saveGroups();
    }

    private void setupPlayerAvatars(@NotNull Player player) {
        // Clear entities from old team
        val activeTeam = player.getTeamManager().getActiveTeam();
        activeTeam.clear();
        Optional.ofNullable(player.getTeamManager().getCurrentTeamInfo()).map(TeamInfo::getAvatars).stream()
            .flatMap(List::stream).map(player.getAvatars()::getAvatarById)
            .filter(Objects::nonNull)
            .map(avatar -> new EntityAvatar(player.getScene(), avatar)).forEach(activeTeam::add);
    }

    private synchronized void removePlayerAvatars(@NotNull Player player) {
        removeEntities(player.getTeamManager().getActiveTeam(), VisionType.VISION_TYPE_MISS);
    }

    public void spawnPlayer(Player player) {
        val teamManager = player.getTeamManager();
        val curAvatarEntity = teamManager.getCurrentAvatarEntity();
        if (isInScene(curAvatarEntity)) return;

        if (curAvatarEntity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) <= 0f) {
            curAvatarEntity.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, 1f);
        }

        addEntity(curAvatarEntity);

        // Notify the client of any extra skill charges
        teamManager.getActiveTeam().stream().map(EntityAvatar::getAvatar).forEach(Avatar::sendSkillExtraChargeMap);
    }

    private void addEntityDirectly(GameEntity entity) {
        this.entities.put(entity.getId(), entity);
        entity.onCreate(); // Call entity create event
    }

    public synchronized void addEntity(GameEntity entity) {
        addEntityDirectly(entity);
        broadcastPacket(new PacketSceneEntityAppearNotify(entity));
    }

    public synchronized void addEntityToSingleClient(Player player, GameEntity entity) {
        addEntityDirectly(entity);
        player.sendPacket(new PacketSceneEntityAppearNotify(entity));

    }

    public void addEntities(Collection<? extends GameEntity> entities) {
        addEntities(entities, VisionType.VISION_TYPE_BORN);
    }

    public void updateEntity(GameEntity entity) {
        this.broadcastPacket(new PacketSceneEntityUpdateNotify(entity));
    }

    public void updateEntity(GameEntity entity, VisionType type) {
        this.broadcastPacket(new PacketSceneEntityUpdateNotify(List.of(entity), type));
    }

    private static <T> List<List<T>> chopped(List<T> list, final int L) {
        return IntStream.range(0, (list.size() + L - 1) / L)
            .mapToObj(i -> list.subList(i * L, Math.min(list.size(), (i + 1) * L)))
            .toList();
    }

    public synchronized void addEntities(Collection<? extends GameEntity> entities, VisionType visionType) {
        if (entities == null || entities.isEmpty()) return;

        entities.forEach(this::addEntityDirectly);

        chopped(entities.stream().toList(), 100).forEach(l -> broadcastPacket(new PacketSceneEntityAppearNotify(l, visionType)));
    }

    private GameEntity removeEntityDirectly(GameEntity entity) {
        val removed = this.entities.remove(entity.getId());
        Optional.ofNullable(removed).ifPresent(GameEntity::onRemoved); //Call entity remove event

        //if(entity instanceof EntityWeapon) {
        //    Grasscutter.getLogger().warn("Weapon removed {}: ", entity.getId());
//
        //    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
        //        Grasscutter.getLogger().warn(ste.toString());
        //    }
        //}
        return removed;
    }

    public void removeEntity(GameEntity entity) {
        removeEntity(entity, VisionType.VISION_TYPE_DIE);
    }

    private synchronized void removeEntity(GameEntity entity, VisionType visionType) {
        Optional.ofNullable(removeEntityDirectly(entity)).ifPresent(removed ->
            broadcastPacket(new PacketSceneEntityDisappearNotify(removed, visionType)));
    }

    public synchronized void removeEntities(Collection<? extends GameEntity> entity, VisionType visionType) {
        val toRemove = entity.stream().filter(Objects::nonNull)
            .map(this::removeEntityDirectly).filter(Objects::nonNull)
            .toList();
        if (!toRemove.isEmpty()) {
            this.broadcastPacket(new PacketSceneEntityDisappearNotify(toRemove, visionType));
        }
    }

    public synchronized void replaceEntity(EntityAvatar oldEntity, EntityAvatar newEntity) {
        removeEntityDirectly(oldEntity);
        addEntityDirectly(newEntity);
        broadcastPacket(new PacketSceneEntityDisappearNotify(oldEntity, VisionType.VISION_TYPE_REPLACE));
        broadcastPacket(new PacketSceneEntityAppearNotify(newEntity, VisionType.VISION_TYPE_REPLACE, oldEntity.getId()));
    }

    public void showOtherEntities(Player player) {
        val entities = this.entities.values().stream().filter(entity ->
            entity != player.getTeamManager().getCurrentAvatarEntity()).toList();

        player.sendPacket(new PacketSceneEntityAppearNotify(entities, VisionType.VISION_TYPE_MEET));
    }

    public void handleAttack(AttackResult result) {
        //GameEntity attacker = getEntityById(result.getAttackerId());
        val target = getEntityById(result.getDefenseId());
        val attackType = ElementType.getTypeByValue(result.getElementType());
        if (target == null) return;

        // God-mode check
        if (target instanceof EntityAvatar entityAvatar) {
            if (entityAvatar.getPlayer().inGodmode()) return;

            if (result.getDamage() != result.getDamageShield()) { // when avatar actually have a shield
                //TODO check
                Optional.ofNullable(this.challenge).ifPresent(c ->
                    c.onDamageMonsterOrShield(getEntityById(result.getAttackerId()), result.getDamageShield() - result.getDamage()));
            }
        }
        target.damage(result.getDamage(), result.getAttackerId(), attackType);
        if (target instanceof EntityGadget gadget) {
            Optional.ofNullable(this.challenge).ifPresent(c -> c.onGadgetDamage(gadget));
        }
    }

    public void killEntity(GameEntity target) {
        killEntity(target, 0);
    }

    public void killEntity(GameEntity target, int attackerId) {
        GameEntity attacker = null;

        if (attackerId > 0) attacker = getEntityById(attackerId);

        if (attacker != null) {
            // Check codex
            if (attacker instanceof EntityClientGadget gadgetAttacker) {
                val clientGadgetOwner = getEntityById(gadgetAttacker.getOwnerEntityId());
                if (clientGadgetOwner instanceof EntityAvatar) {
                    ((EntityClientGadget) attacker).getOwner().getCodex().checkAnimal(target, CodexAnimalData.CountType.CODEX_COUNT_TYPE_KILL);
                }
            } else if (attacker instanceof EntityAvatar avatarAttacker) {
                avatarAttacker.getPlayer().getCodex().checkAnimal(target, CodexAnimalData.CountType.CODEX_COUNT_TYPE_KILL);
            }


        }

        // Packet
        broadcastPacket(new PacketLifeStateChangeNotify(attackerId, target, LifeState.LIFE_DEAD));

        // Remove entity from world
        removeEntity(target);

        // Death event
        target.onDeath(attackerId);
        // Reward drop
        if (target instanceof EntityMonster monster) {
            if (getSceneType() != SceneType.SCENE_DUNGEON && attacker != null) {
                getWorld().getServer().getDropSystem().callDrop(monster);
            }
            triggerDungeonEvent(DungeonPassConditionType.DUNGEON_COND_KILL_MONSTER_COUNT, ++this.killedMonsterCount);
        } else if (target instanceof EntityGadget gadget) {
            Optional.ofNullable(gadget.getGadgetData())
                .filter(data -> data.getType() == EntityType.Chest)
                .ifPresent(data -> this.killChestCount++);
        }
    }

    public void onTick() {
        // disable script for home
        if (getSceneType() == SceneType.SCENE_HOME_WORLD || getSceneType() == SceneType.SCENE_HOME_ROOM) {
            finishLoading();
            return;
        }
        if (this.scriptManager.isInit()) {
            //this.checkBlocks();
            if (this.tickCount % 2 == 0) {
                checkGroups();
            }
        } else {
            // TEMPORARY
            checkSpawns();
        }
        // Triggers
        this.scriptManager.checkRegions();

        Optional.ofNullable(this.challenge).ifPresent(WorldChallenge::onCheckTimeOut);

        this.entities.values().forEach(e -> e.onTick(getSceneTimeSeconds()));

        checkNpcGroup();
        finishLoading();
        checkPlayerRespawn();
        if (this.tickCount % 10 == 0) {
            broadcastPacket(new PacketSceneTimeNotify(this));
        }
        this.tickCount++;
    }

    private void checkPlayerRespawn() {
        if (this.scriptManager.getConfig() == null) return;

        val diePos = this.scriptManager.getConfig().getDie_y();
        // Check if we need a respawn
        this.players.stream().filter(p -> diePos >= p.getPosition().getY()).forEach(this::respawnPlayer);
        this.entities.values().stream().filter(e -> diePos >= e.getPosition().getY()).forEach(this::killEntity);
    }

    private Position getDefaultLocation(Player player) {
        return Optional.ofNullable(this.scriptManager.getConfig().getBorn_pos()).orElse(player.getPosition());
    }

    private Position getDefaultRot(Player player) {
        return Optional.ofNullable(this.scriptManager.getConfig().getBorn_rot()).orElse(player.getRotation());
    }

    private Position getRespawnLocation(Player player) {
        //TODO get last valid location the player stood on
        return Optional.ofNullable(this.dungeonManager).map(DungeonManager::getRespawnLocation).orElse(getDefaultLocation(player));
    }

    private Position getRespawnRotation(Player player) {
        return Optional.ofNullable(this.dungeonManager).map(DungeonManager::getRespawnRotation).orElse(getDefaultRot(player));

    }

    public boolean respawnPlayer(Player player) {
        player.getTeamManager().onAvatarDieDamage();

        // todo should probably respawn the player at the last valid location
        return this.world.transferPlayerToScene(player, TeleportProperties.builder()
            .sceneId(getId())
            .prevSceneId(getId())
            .prevPos(player.getPosition())
            .teleportTo(getRespawnLocation(player))
            .teleportRot(getRespawnRotation(player))
            .teleportType(PlayerTeleportEvent.TeleportType.INTERNAL)
            .worldType(Optional.ofNullable(this.dungeonManager).map(data -> 13).orElse(14))
            .enterType(EnterTypeOuterClass.EnterType.ENTER_TYPE_GOTO)
            .dungeonId(Optional.ofNullable(this.dungeonManager).map(DungeonManager::getDungeonData).map(DungeonData::getId).orElse(0))
            .enterReason(this.dungeonManager != null ? EnterReason.DungeonReviveOnWaypoint : EnterReason.Revival)
            .build());
    }

    private void finishLoading() {
        if (this.finishedLoading) return;

        this.finishedLoading = true;
        this.afterLoadedCallbacks.forEach(Runnable::run);
        this.afterLoadedCallbacks.clear();
    }

    public void runWhenFinished(Runnable runnable) {
        if (this.finishedLoading) {
            runnable.run();
            return;
        }
        this.afterLoadedCallbacks.add(runnable);
    }

    private int getEntityLevel(int baseLevel, int worldLevelOverride) {
        return Math.max(1, Math.min(Math.max(worldLevelOverride + baseLevel - 22, baseLevel), 100));
    }

    private void checkNpcGroup() {
        Set<SceneNpcBornEntry> npcBornEntries = ConcurrentHashMap.newKeySet();
        npcBornEntries.addAll(this.players.stream().map(this::loadNpcForPlayer).flatMap(List::stream).toList());

        // clear the unreachable group for client
        val toUnload = this.npcBornEntrySet.stream()
            .filter(i -> !npcBornEntries.contains(i))
            .map(SceneNpcBornEntry::getGroupId)
            .toList();

        if (!toUnload.isEmpty()) {
            broadcastPacket(new PacketGroupUnloadNotify(toUnload));
            Grasscutter.getLogger().debug("Unload NPC Group {}", toUnload);
        }
        // exchange the new npcBornEntry Set
        this.npcBornEntrySet = npcBornEntries;
    }

    private synchronized void checkSpawns() {
        val loadedGridBlocks = this.players.stream()
            .map(p -> SpawnDataEntry.GridBlockId.getAdjacentGridBlockIds(p.getSceneId(), p.getPosition()))
            .flatMap(Arrays::stream).collect(Collectors.toSet());

        // Don't recalculate static spawns if nothing has changed
        if (this.loadedGridBlocks.containsAll(loadedGridBlocks)) return;

        this.loadedGridBlocks = loadedGridBlocks;
        val visible = loadedGridBlocks.stream().map(GameDepot.getSpawnLists()::get)
            .filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toSet());

        // World level
        final int worldLevelOverride = Optional.ofNullable(GameData.getWorldLevelDataMap().get(this.world.getWorldLevel()))
            .map(WorldLevelData::getMonsterLevel).orElse(0);

        // Todo
        List<GameEntity> toAdd = new ArrayList<>();
        List<GameEntity> toRemove = new ArrayList<>();
        val spawnedEntities = this.spawnedEntities;
        for (SpawnDataEntry entry : visible) {
            // If spawn entry is in our view and hasn't been spawned/killed yet, we should spawn it
            if (spawnedEntities.contains(entry) || this.deadSpawnedEntities.contains(entry)) continue;
            // Entity object holder
            GameEntity entity = null;

            // Check if spawn entry is monster or gadget
            if (entry.getMonsterId() > 0) {
                val data = GameData.getMonsterDataMap().get(entry.getMonsterId());
                if (data == null) continue;

                final int level = getEntityLevel(entry.getLevel(), worldLevelOverride);

                val monster = new EntityMonster(this, data, entry.getPos(), level);
                monster.getRotation().set(entry.getRot());
                monster.setGroupId(entry.getGroup().getGroupId());
                monster.setPoseId(entry.getPoseId());
                monster.setConfigId(entry.getConfigId());
                monster.setSpawnEntry(entry);

                entity = monster;
            } else if (entry.getGadgetId() > 0) {
                val gadget = new EntityGadget(this, entry.getGadgetId(), entry.getPos(), entry.getRot());
                gadget.setGroupId(entry.getGroup().getGroupId());
                gadget.setConfigId(entry.getConfigId());
                gadget.setSpawnEntry(entry);
                int state = entry.getGadgetState();
                if (state > 0) {
                    gadget.setState(state);
                }
                gadget.buildContent();

                gadget.setFightProperty(FightProperty.FIGHT_PROP_BASE_HP, Float.POSITIVE_INFINITY);
                gadget.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, Float.POSITIVE_INFINITY);
                gadget.setFightProperty(FightProperty.FIGHT_PROP_MAX_HP, Float.POSITIVE_INFINITY);

                entity = gadget;
            }

            if (entity == null) continue;

            // Add to scene and spawned list
            toAdd.add(entity);
            spawnedEntities.add(entry);
        }

        this.entities.values().stream().filter(entity -> entity.getSpawnEntry() != null)
            .filter(entity -> !(entity instanceof EntityWeapon))
            .filter(entity -> !visible.contains(entity.getSpawnEntry()))
            .peek(toRemove::add).map(GameEntity::getSpawnEntry).forEach(spawnedEntities::remove);

        if (!toAdd.isEmpty()) {
            toAdd.forEach(this::addEntityDirectly);
            broadcastPacket(new PacketSceneEntityAppearNotify(toAdd, VisionType.VISION_TYPE_BORN));
        }
        if (!toRemove.isEmpty()) {
            toRemove.forEach(this::removeEntityDirectly);
            broadcastPacket(new PacketSceneEntityDisappearNotify(toRemove, VisionType.VISION_TYPE_REMOVE));
        }
    }

    public List<SceneBlock> getPlayerActiveBlocks(Player player) {
        // consider the borders' entities of blocks, so we check if contains by index
        return SceneIndexManager.queryNeighbors(getScriptManager().getBlocksIndex(),
            player.getPosition().toXZDoubleArray(), Grasscutter.getConfig().server.game.loadEntitiesForPlayerRange);
    }

    private Set<Integer> getPlayerActiveGroups(Player player) {
        // consider the borders' entities of blocks, so we check if contains by index
        return IntStream.range(0, 4)
            .mapToObj(i -> getScriptManager().getGroupGrids().get(i).getNearbyGroups(i, player.getPosition()))
            .flatMap(Set::stream).collect(Collectors.toSet());
    }

    public synchronized boolean loadBlock(SceneBlock block) {
        if (this.loadedBlocks.contains(block)) return false;

        onLoadBlock(block, this.players);
        this.loadedBlocks.add(block);
        return true;
    }

    /**
     * Periodically checks for groups spawned, mainly
     * 1) checks if currently loaded groups are out of player's sight
     *  - if so, remove those groups
     * 2) check if there are new groups came into player's sight
     *  - if so, load up those groups
     * */
    private synchronized void checkGroups() {
        val playerMoved = this.players.stream()
            .filter(p -> p.getLastCheckedPosition() == null || !p.getLastCheckedPosition().equal2d(p.getPosition()))
            .peek(p -> p.setLastCheckedPosition(p.getPosition().clone())).findAny();

        if(playerMoved.isEmpty()) return;

        val visible = this.players.stream().map(this::getPlayerActiveGroups)
            .flatMap(Collection::stream).collect(Collectors.toSet());

        this.loadedGroups.stream().filter(group -> !visible.contains(group.getId()) && !group.isDynamic_load())
            .forEach(group -> unloadGroup(this.scriptManager.getBlocks().get(group.block_id), group.getId()));

        val toLoad = visible.stream().filter(g -> this.loadedGroups.stream().noneMatch(gr -> gr.getId() == g))
            .filter(g -> !this.replacedGroup.contains(g)).map(g -> Optional.ofNullable(this.scriptManager.getBlocks())
                .stream().map(Map::values).flatMap(Collection::stream).peek(this::loadBlock).map(b -> b.getGroups().get(g))
                .filter(Objects::nonNull).filter(group -> !group.isDynamic_load()).findFirst().orElse(null))
            .filter(Objects::nonNull).toList();

        onLoadGroup(toLoad);
        if(!toLoad.isEmpty()) onRegisterGroups();
    }

    private Set<SceneGroup> onLoadBlock(SceneBlock block, List<Player> players) {
        if (!block.isLoaded()) {
            this.scriptManager.loadBlockFromScript(block);
            Grasscutter.getLogger().info("Scene {} Block {} loaded.", getId(), block.getId());
        }
        return this.scriptManager.getLoadedGroupSetPerBlock().computeIfAbsent(block.getId(), f -> new HashSet<>());
    }

    /**
     * Load specific (dynamic loaded) group
     * don't load the group if it was replaced by other groups, TODO should probably log the failed loading reason
     * */
    public int loadDynamicGroup(int groupId) {
        return this.scriptManager.getGroupInstanceById(groupId) != null || this.replacedGroup.contains(groupId) ? -1 :
            Optional.ofNullable(this.scriptManager.getGroupById(groupId))
                .map(SceneGroup::getInit_config).map(SceneInitConfig::getSuite).orElse(-1);
    }

    public boolean unregisterDynamicGroup(int groupId){
        val group = this.scriptManager.getGroupById(groupId);
        if(group == null) return false;

        val block = this.scriptManager.getBlocks().get(group.block_id);
        unloadGroup(block, groupId);

        val toRestore = Optional.ofNullable(block.getGroups().get(groupId)).map(g -> g.getReplaceableGroups(block.getGroups().values()))
            .stream().flatMap(List::stream).filter(replacement -> this.replacedGroup.remove(replacement.getId())).toList();
        if (!toRestore.isEmpty()) {
            onLoadGroup(toRestore);
            Grasscutter.getLogger().info("Unregistered group: {}", groupId);
            Grasscutter.getLogger().info("Replaced groups: {}", this.replacedGroup);
        }
        return true;
    }

    /**
     * Check if a spawning group(1) will replace other groups(2). If so, unload groups(2)
     * */
    public void onRegisterGroups() {
        // Create the graph
        val groupList = new HashSet<Integer>();
        val nodes = GameData.getGroupReplacements().values().stream()
            .filter(replacement -> this.loadedGroups.stream().filter(group -> group.isDynamic_load())
                .anyMatch(group -> group.getId() == replacement.id)) // dynamic groups
//            .filter(replacement -> getReplacedGroup().stream().noneMatch(replacement.replace_groups::contains))
            .peek(replacement -> Grasscutter.getLogger().info("Graph ordering replacement {}", replacement))
            .peek(replacement -> groupList.add(replacement.id))
            .peek(replacement -> groupList.addAll(replacement.replace_groups))
            .map(replacement -> replacement.replace_groups
                .stream().map(id -> new KahnsSort.Node(replacement.id, id)).toList())
            .flatMap(List::stream)
            .collect(Collectors.toSet());

        // Now we can start unloading and loading groups :D
        Optional.ofNullable(KahnsSort.doSort(new KahnsSort.Graph(
                nodes.stream().toList(), groupList.stream().toList()))).stream().flatMap(List::stream)
            .map(groupId -> this.loadedGroups.stream().filter(g -> g.getId() == groupId).findFirst()) // isGroupJoinReplacement
            .filter(Optional::isPresent).map(Optional::get)
            .map(targetGroup -> targetGroup.getReplaceableGroups(this.loadedGroups))
            .flatMap(List::stream)
            .filter(replacement -> !this.replacedGroup.contains(replacement.getId()))
            .peek(replacement -> this.replacedGroup.add(replacement.getId()))
            .peek(replacement -> Grasscutter.getLogger().info("Graph ordering: unloaded {}", replacement.getId()))
            .peek(replacement -> Grasscutter.getLogger().info("Replaced groups: {}", this.replacedGroup))
            .forEach(replacement -> unloadGroup(this.scriptManager.getBlocks().get(replacement.block_id), replacement.getId()));
    }

    public void loadTriggerFromGroup(SceneGroup group, String triggerName) {
        //Load triggers and regions
        this.scriptManager.registerTrigger(group.getTriggers().values().stream().filter(p -> p.getName().contains(triggerName)).toList());
        group.getRegions().values().stream().filter(q -> q.getConfig_id() == Integer.parseInt(triggerName.substring(13)))
            .map(region -> new EntityRegion(this, region)).forEach(this.scriptManager::registerRegion);
    }

    /**
     * Load specific group(s), used when this
     * 1) group is came into player's sight (or visible)
     * 2) group is being registered
     * */
    public void onLoadGroup(List<SceneGroup> groups) {
        if (groups == null || groups.isEmpty()) return;

        groups.stream().filter(Objects::nonNull).filter(group -> !this.loadedGroups.contains(group))
            // We load the script files for the groups here
            .peek(this.scriptManager::loadGroupFromScript)
            .forEach(group -> onLoadBlock(this.scriptManager.getBlocks().get(group.block_id), getPlayers()).add(group));

        // Spawn gadgets AFTER triggers are added
        // TODO
        val entities = new ArrayList<GameEntity>();
        val entitiesBorn = new ArrayList<GameEntity>();
        groups.stream().filter(group -> !this.loadedGroups.contains(group)).filter(group -> group.getInit_config() != null)
            .map(group -> Optional.ofNullable(this.scriptManager.getCachedGroupInstanceById(group.getId()))
                .stream().peek(cachedInstance -> cachedInstance.setLuaGroup(group))
                .findFirst().orElse(this.scriptManager.getGroupInstanceById(group.getId())))
            .peek(gi -> this.loadedGroups.add(gi.getLuaGroup())) // Load suites
            .forEach(gi -> this.scriptManager.refreshGroup(gi, 0, false, entitiesBorn)); //This is what the official server does

        this.scriptManager.meetEntities(entities);
        this.scriptManager.addEntities(entitiesBorn);
        groups.forEach(g -> this.scriptManager.callEvent(new ScriptArgs(g.getId(), EventType.EVENT_GROUP_LOAD, g.getId())));
        Grasscutter.getLogger().info("Scene {} loaded {} group(s)", getId(), groups.size());
    }

    /**
     * Remove specific group, used when this
     * 1) group is not within player's sight (or not visible)
     * 2) group is replaced by other groups
     * 3) group is being unregistered
     * */
    private void unloadGroup(SceneBlock block, int groupId) {
        removeEntities(this.entities.values().stream().filter(Objects::nonNull).filter(e ->
            e.getBlockId() == block.getId() && e.getGroupId() == groupId).toList(), VisionType.VISION_TYPE_REMOVE);


        SceneGroup group = block.getGroups().get(groupId);
        val triggers = group.getTriggers();
        if (triggers != null) {
            triggers.values().forEach(getScriptManager()::deregisterTrigger);
        }
        val regions = group.getRegions();
        if (regions != null) {
            regions.values().forEach(getScriptManager()::deregisterRegion);
        }

        Optional.ofNullable(this.scriptManager.getLoadedGroupSetPerBlock().get(block.getId())).ifPresent(s -> s.remove(group));

        this.loadedGroups.remove(group);

        if (this.scriptManager.getLoadedGroupSetPerBlock().get(block.getId()).isEmpty()) {
            this.scriptManager.getLoadedGroupSetPerBlock().remove(block.getId());
            Grasscutter.getLogger().info("Scene {} Block {} is unloaded.", getId(), block.getId());
        }

        broadcastPacket(new PacketGroupUnloadNotify(List.of(groupId)));
        this.scriptManager.unregisterGroup(group);
    }

    // Gadgets

    public void onPlayerCreateGadget(EntityClientGadget gadget) {
        // Directly add
        addEntityDirectly(gadget);

        // Add to owner's gadget list
        gadget.getOwner().getTeamManager().getGadgets().add(gadget);

        // Optimization
        if (this.players.stream().anyMatch(p -> p != gadget.getOwner())) { // if there is other players in scene
            broadcastPacketToOthers(gadget.getOwner(), new PacketSceneEntityAppearNotify(gadget));
        }
    }

    public void onPlayerDestroyGadget(int entityId) {
        if (!(this.entities.get(entityId) instanceof EntityClientGadget gadget)) return;

        // Get and remove entity
        removeEntityDirectly(gadget);

        // Remove from owner's gadget list
        gadget.getOwner().getTeamManager().getGadgets().remove(gadget);

        // Optimization
        if (this.players.stream().anyMatch(p -> p != gadget.getOwner())) { // if there is other players in scene
            broadcastPacketToOthers(gadget.getOwner(), new PacketSceneEntityDisappearNotify(gadget, VisionType.VISION_TYPE_DIE));
        }
    }

    // Broadcasting
    /**
     * Send packets to all players in world
     * */
    public void broadcastPacket(BasePacket packet) {
        // Send to all players - might have to check if player has been sent data packets
        broadcastPacketToOthers(null, packet);
    }

    /**
     * Send packets to all players in world excluding one, which normally is the owner
     * */
    public void broadcastPacketToOthers(Player excludedPlayer, BasePacket packet) {
        this.players.stream().filter(p -> p != excludedPlayer).forEach(p -> p.sendPacket(packet));
    }

    public void addItemEntity(int itemId, int amount, GameEntity bornForm) {
        val itemData = GameData.getItemDataMap().get(itemId);
        if (itemData == null) return;

        if (itemData.isEquip()) {
            val range = (1.5f + (.05f * amount));
            for (int i = 0; i < amount; i++) {
                val pos = bornForm.getPosition().nearby2d(range).addZ(.9f);  // Why Z?
                addEntity(new EntityItem(this, null, itemData, pos, 1));
            }
        } else {
            addEntity(new EntityItem(this, null, itemData, bornForm.getPosition().clone().addZ(.9f), amount));
        }
    }

    public void loadNpcForPlayerEnter(Player player) {
        this.npcBornEntrySet.addAll(loadNpcForPlayer(player));
    }

    private List<SceneNpcBornEntry> loadNpcForPlayer(Player player) {
        val pos = player.getPosition();
        val data = GameData.getSceneNpcBornData().get(getId());
        if (data == null) return List.of();

        val npcList = SceneIndexManager.queryNeighbors(data.getIndex(), pos.toDoubleArray(),
            Grasscutter.getConfig().server.game.loadEntitiesForPlayerRange);

        val sceneNpcBornEntries = npcList.stream().filter(i -> !this.npcBornEntrySet.contains(i)).toList();

        if (!sceneNpcBornEntries.isEmpty()) {
            broadcastPacket(new PacketGroupSuiteNotify(sceneNpcBornEntries));
            Grasscutter.getLogger().debug("Loaded Npc Group Suite {}", sceneNpcBornEntries);
        }
        return npcList;
    }

    public void loadGroupForQuest(List<QuestGroupSuite> sceneGroupSuite) {
        if (!this.scriptManager.isInit()) return;

        sceneGroupSuite.stream().filter(i -> this.scriptManager.getGroupById(i.getGroup()) != null)
            .forEach(i -> this.scriptManager.refreshGroup(i.getGroup(), i.getSuite(), false));
    }

    public void unlockForce(int force) {
        this.unlockedForces.add(force);
        broadcastPacket(new PacketSceneForceUnlockNotify(force, true));
    }

    public void lockForce(int force) {
        this.unlockedForces.remove(force);
        broadcastPacket(new PacketSceneForceLockNotify(force));
    }

    public void selectWorktopOptionWith(SelectWorktopOptionReqOuterClass.SelectWorktopOptionReq req) {
        val entity = getEntityById(req.getGadgetEntityId());
        Optional.ofNullable(entity)
            .filter(EntityGadget.class::isInstance).map(EntityGadget.class::cast)
            .map(EntityGadget::getContent)
            .filter(GadgetWorktop.class::isInstance).map(GadgetWorktop.class::cast)
            .filter(worktop -> worktop.onSelectWorktopOption(req))
            .ifPresent(worktop -> entity.getScene().removeEntity(entity, VisionType.VISION_TYPE_REMOVE));
    }

    public void saveGroups() {
        this.scriptManager.getCachedGroupInstances().values().forEach(SceneGroupInstance::save);
    }
}
