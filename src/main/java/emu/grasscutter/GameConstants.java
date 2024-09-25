package emu.grasscutter;

import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.Utils;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public final class GameConstants {
    public static String VERSION = "3.2.0";

    public static final int DEFAULT_TEAMS = 4;
    public static final int MAX_TEAMS = 10;
    public static final int MAIN_CHARACTER_MALE = 10000005;
    public static final int MAIN_CHARACTER_FEMALE = 10000007;
    public static final Position START_POSITION = new Position(2747, 194, -1719);

    public static final int MAX_FRIENDS = 60;
    public static final int MAX_FRIEND_REQUESTS = 50;

    public static final int SERVER_CONSOLE_UID = 99; // The UID of the server console's "player".

    public static final int BATTLE_PASS_MAX_LEVEL = 50;
    public static final int BATTLE_PASS_POINT_PER_LEVEL = 1000;
    public static final int BATTLE_PASS_POINT_PER_WEEK = 10000;
    public static final int BATTLE_PASS_LEVEL_PRICE = 150;
    public static final int BATTLE_PASS_CURRENT_INDEX = 2;
    // TODO move them to utils
    public static final DateTimeFormatter TIME_FORMATTER_TIME_ONLY = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter TIME_FORMATTER_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final ZoneId ZONE_ID = ZoneId.of(Grasscutter.getConfig().server.game.timeZone);
    public static final DayOfWeek WEEKLY_BOSS_RESIN_DISCOUNT_REFRESH_DAY = DayOfWeek.MONDAY;
    public static final int WEEKLY_BOSS_RESIN_DISCOUNT_REFRESH_DAY_INTERVAL = 7;
    public static final int REFRESH_HOUR = 4;
    public static final int WEEKLY_BOSS_RESIN_DISCOUNT_COUNT = 3;
    public static final float WEEKLY_BOSS_RESIN_DISCOUNT_VALUE = 0.5f;

    // Default entity ability hashes.
    public static final String[] DEFAULT_ABILITY_STRINGS = {
        //ConfigAbility_Avatar_AllDefault
        "Avatar_DefaultAbility_VisionReplaceDieInvincible",
        "Avatar_DefaultAbility_AvartarInShaderChange",

        //ConfigAbility_Avatar_Player_Common
        "Avatar_PlayerBoy_ExtraAttack_Common",
        "Avatar_PlayerGirl_ExtraAttack_Common",
        "Avatar_PlayerBoy_ExtraAttack",
        "Avatar_PlayerGirl_ExtraAttack",
        "Avatar_Player_ExtraAttack_Damage",
        "Avatar_PlayerBoy_NormalAttack_DamageHandler",
        "Avatar_PlayerGirl_NormalAttack_DamageHandler",
        "Avatar_PlayerBoy_FallingAnthem",
        "Avatar_PlayerGirl_FallingAnthem",
        "Avatar_Trampoline_Jump_Controller",
        "Avatar_Trampoline_Jump_SLC",

        //ConfigAbility_Avatar_Common
        "Avatar_SprintBS_Invincible",
        "Avatar_Freeze_Duration_Reducer",
        "Avatar_Girl_Catalyst_HitFallToGround_Hide",
        "Avatar_Bow_Aim_EmoController",
        "Avatar_RockGadget_Summon_Detect",
        "RocketGadget_WeightRatio",
        "Avatar_Common_Achievement_Listener",
        "Avatar_FallAnthem_Achievement_Listener",
        "Avatar_Watcher_SnowMountain_MPPlay",
        "Avatar_Attack_ReviveEnergy",
        "Avatar_Component_Initializer",
        "Avatar_PlayMusic_Lyre_Controller",
        "Avatar_Watcher_SuperTrial_RangeKillEnemy",
//        "Avatar_RockGadget_KillByBlunt", //BAD. Don't use.
//        "Avatar_ElementReaction_Test", //BAD. Don't use.
        "Avatar_HDMesh_Controller",
        "Avatar_ArkheGrade_CD_Controller",
        "Avatar_FluidAgitator",
        "Avatar_TriggerNyxInstant",
        "Avatar_NyxState_Listener"
    };
    public static final int[] DEFAULT_ABILITY_HASHES = Arrays.stream(DEFAULT_ABILITY_STRINGS).mapToInt(Utils::abilityHash).toArray();
    public static final int DEFAULT_ABILITY_NAME = Utils.abilityHash("Default");
}
