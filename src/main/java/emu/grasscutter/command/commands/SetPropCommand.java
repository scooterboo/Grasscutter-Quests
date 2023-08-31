package emu.grasscutter.command.commands;

import java.util.*;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.TowerFloorData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.tower.TowerFloorRecordInfo;
import emu.grasscutter.server.packet.send.PacketOpenStateChangeNotify;
import emu.grasscutter.server.packet.send.PacketSceneAreaUnlockNotify;
import emu.grasscutter.server.packet.send.PacketScenePointUnlockNotify;
import lombok.val;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@Command(label = "setProp", aliases = {"prop"}, usage = {"<prop> <value>"}, permission = "player.setprop", permissionTargeted = "player.setprop.others")
public final class SetPropCommand implements CommandHandler {
    enum PseudoProp {
        NONE,
        WORLD_LEVEL,
        TOWER_LEVEL,
        BP_LEVEL,
        GOD_MODE,
        UNLIMITED_STAMINA,
        UNLIMITED_ENERGY,
        SET_OPENSTATE,
        UNSET_OPENSTATE,
        UNLOCK_MAP
    }

    static class Prop {
        private final String name;
        private final PlayerProperty prop;
        private final PseudoProp pseudoProp;

        public Prop(PlayerProperty prop) {
            this(prop.toString(), prop, PseudoProp.NONE);
        }

        public Prop(String name) {
            this(name, PlayerProperty.PROP_NONE, PseudoProp.NONE);
        }

        public Prop(String name, PseudoProp pseudoProp) {
            this(name, PlayerProperty.PROP_NONE, pseudoProp);
        }

        public Prop(String name, PlayerProperty prop) {
            this(name, prop, PseudoProp.NONE);
        }

        public Prop(String name, PlayerProperty prop, PseudoProp pseudoProp) {
            this.name = name;
            this.prop = prop;
            this.pseudoProp = pseudoProp;
        }
    }

    private final Map<String, Prop> props = new HashMap<>();

    public SetPropCommand() {
        // Full PlayerProperty enum that won't be advertised but can be used by devs
        for (val prop : PlayerProperty.values()) {
            val name = prop.toString().substring(5);  // PROP_EXP -> EXP
            val key = name.toLowerCase();  // EXP -> exp
            this.props.put(key, new Prop(name, prop));
            this.props.put(prop.name().toLowerCase(), new Prop(prop.name(), prop));
        }
        // Add special props
        val worldLevel = new Prop("World Level", PlayerProperty.PROP_PLAYER_WORLD_LEVEL, PseudoProp.WORLD_LEVEL);
        this.props.put("worldlevel", worldLevel);
        this.props.put("wl", worldLevel);

        val abyss = new Prop("Tower Level", PseudoProp.TOWER_LEVEL);
        this.props.put("abyss", abyss);
        this.props.put("abyssfloor", abyss);
        this.props.put("ut", abyss);
        this.props.put("tower", abyss);
        this.props.put("towerlevel", abyss);
        this.props.put("unlocktower", abyss);

        val bpLevel = new Prop("BP Level", PseudoProp.BP_LEVEL);
        this.props.put("bplevel", bpLevel);
        this.props.put("bp", bpLevel);
        this.props.put("battlepass", bpLevel);

        val godMode = new Prop("GodMode", PseudoProp.GOD_MODE);
        this.props.put("godmode", godMode);
        this.props.put("god", godMode);

        val noStamina = new Prop("UnlimitedStamina", PseudoProp.UNLIMITED_STAMINA);
        this.props.put("unlimitedstamina", noStamina);
        this.props.put("us", noStamina);
        this.props.put("nostamina", noStamina);
        this.props.put("nostam", noStamina);
        this.props.put("ns", noStamina);

        val unlimitedEnergy = new Prop("UnlimitedEnergy", PseudoProp.UNLIMITED_ENERGY);
        this.props.put("unlimitedenergy", unlimitedEnergy);
        this.props.put("ue", unlimitedEnergy);

        val setOpenState = new Prop("SetOpenstate", PseudoProp.SET_OPENSTATE);
        this.props.put("setopenstate", setOpenState);
        this.props.put("so", setOpenState);

        val unsetOpenState = new Prop("UnsetOpenstate", PseudoProp.UNSET_OPENSTATE);
        this.props.put("unsetopenstate", unsetOpenState);
        this.props.put("uo", unsetOpenState);

        val unlockMap = new Prop("UnlockMap", PseudoProp.UNLOCK_MAP);
        this.props.put("unlockmap", unlockMap);
        this.props.put("um", unlockMap);
    }

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() != 2) {
            sendUsageMessage(sender);
            return;
        }
        val propStr = args.get(0).toLowerCase();
        val valueStr = args.get(1).toLowerCase();
        int value;

        if (!props.containsKey(propStr)) {
            sendUsageMessage(sender);
            return;
        }
        try {
            value = switch (valueStr.toLowerCase()) {
                case "on", "true" -> 1;
                case "off", "false" -> 0;
                case "toggle" -> -1;
                default -> Integer.parseInt(valueStr);
            };
        } catch (NumberFormatException ignored) {
            CommandHandler.sendTranslatedMessage(sender, "commands.execution.argument_error");
            return;
        }

        val prop = props.get(propStr);
        val success = switch (prop.pseudoProp) {
            case WORLD_LEVEL -> targetPlayer.setWorldLevel(value);
            case BP_LEVEL -> targetPlayer.getBattlePassManager().setLevel(value);
            case TOWER_LEVEL -> this.setTowerLevel(sender, targetPlayer, value);
            case GOD_MODE, UNLIMITED_STAMINA, UNLIMITED_ENERGY -> this.setBool(sender, targetPlayer, prop.pseudoProp, value);
            case SET_OPENSTATE -> this.setOpenState(targetPlayer, value, 1);
            case UNSET_OPENSTATE -> this.setOpenState(targetPlayer, value, 0);
            case UNLOCK_MAP -> unlockMap(targetPlayer);
            default -> targetPlayer.setProperty(prop.prop, value);
        };

        if (success) {
            if (targetPlayer == sender) {
                CommandHandler.sendTranslatedMessage(sender, "commands.generic.set_to", prop.name, valueStr);
            } else {
                val uidStr = targetPlayer.getAccount().getId();
                CommandHandler.sendTranslatedMessage(sender, "commands.generic.set_for_to", prop.name, uidStr, valueStr);
            }
        } else {
            if (prop.prop != PlayerProperty.PROP_NONE) {  // PseudoProps need to do their own error messages
                final int min = targetPlayer.getPropertyMin(prop.prop);
                final int max = targetPlayer.getPropertyMax(prop.prop);
                CommandHandler.sendTranslatedMessage(sender, "commands.generic.invalid.value_between", prop.name, min, max);
            }
        }
    }

    private boolean setTowerLevel(Player sender, Player targetPlayer, int topFloor) {
        List<Integer> floorIds = targetPlayer.getServer().getTowerSystem().getAllFloors();
        if (topFloor < 0 || topFloor > floorIds.size()) {
            CommandHandler.sendTranslatedMessage(sender, "commands.generic.invalid.value_between", "Tower Level", 0, floorIds.size());
            return false;
        }

        val recordMap = targetPlayer.getTowerManager().getRecordMap();
        // Add records for each unlocked floor
        floorIds.subList(0, topFloor).stream().map(floorId -> GameData.getTowerFloorDataMap().get(floorId.intValue()))
            .filter(Objects::nonNull).forEach(floorData -> recordMap.putIfAbsent(
                floorData.getFloorIndex(), TowerFloorRecordInfo.create(floorData.getFloorId())));

        // Remove records for each floor past our target
        floorIds.subList(topFloor, floorIds.size()).stream().map(floorId -> GameData.getTowerFloorDataMap().get(floorId.intValue()))
            .map(TowerFloorData::getFloorIndex).forEach(recordMap::remove);

        // Six stars required on Floor 8 to unlock Floor 9+
        if (topFloor > 8) {
            val floorData = GameData.getTowerFloorDataMap().get(floorIds.get(7).intValue());
            val recordInfo = recordMap.get(8);
            if (floorData != null && recordInfo != null) {
                GameData.getTowerLevelDataMap().values().stream().filter(data -> data.getLevelGroupId() == floorData.getLevelGroupId())
                    .forEach(levelData -> recordInfo.update(levelData.getId(), levelData.getLevelIndex(), 2));
            }
        }

        targetPlayer.getTowerManager().notifyFloorChange(recordMap.values().stream().toList());
        return true;
    }

    private boolean setBool(Player sender, Player targetPlayer, PseudoProp pseudoProp, int value) {
        boolean enabled = switch (pseudoProp) {
            case GOD_MODE -> targetPlayer.inGodmode();
            case UNLIMITED_STAMINA -> targetPlayer.getUnlimitedStamina();
            case UNLIMITED_ENERGY -> !targetPlayer.getEnergyManager().getEnergyUsage();
            default -> false;
        };
        enabled = switch (value) {
            case -1 -> !enabled;
            case 0 -> false;
            default -> true;
        };

        switch (pseudoProp) {
            case GOD_MODE -> targetPlayer.setGodmode(enabled);
            case UNLIMITED_STAMINA -> targetPlayer.setUnlimitedStamina(enabled);
            case UNLIMITED_ENERGY -> targetPlayer.getEnergyManager().setEnergyUsage(!enabled);
            default -> {
                return false;
            }
        }
        return true;
    }

    private boolean setOpenState(Player targetPlayer, int state, int value) {
        targetPlayer.sendPacket(new PacketOpenStateChangeNotify(state, value));
        return true;
    }

    // List of map areas. Unfortunately, there is no readily available source for them in excels or bins.
    final static private List<Integer> sceneAreas = List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,17,18,19,20,21,22,23,24,25,26,27,28,29,100,101,102,103,200,210,300,400,401,402,403);
    private boolean unlockMap(Player targetPlayer) {
        // Unlock.
        GameData.getScenePointsPerScene().forEach((sceneId, scenePoints) -> {
            // Unlock trans points.
            targetPlayer.getUnlockedScenePoints(sceneId).addAll(scenePoints);

            // Unlock map areas.
            targetPlayer.getUnlockedSceneAreas(sceneId).addAll(sceneAreas);
        });

        // Send notify.
        int playerScene = targetPlayer.getSceneId();
        targetPlayer.sendPacket(new PacketScenePointUnlockNotify(playerScene, targetPlayer.getUnlockedScenePoints(playerScene)));
        targetPlayer.sendPacket(new PacketSceneAreaUnlockNotify(playerScene, targetPlayer.getUnlockedSceneAreas(playerScene)));
        return true;
    }
}
