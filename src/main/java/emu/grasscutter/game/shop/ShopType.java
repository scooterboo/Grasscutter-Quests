package emu.grasscutter.game.shop;

public enum ShopType{
    SHOP_TYPE_NONE(0),
    SHOP_TYPE_PAIMON(900),
    SHOP_TYPE_PACKAGE(902),
    SHOP_TYPE_MCOIN(903),
    SHOP_TYPE_RECOMMEND(1001),
    SHOP_TYPE_CITY(1002),
    SHOP_TYPE_BLACKSMITH(1003),
    SHOP_TYPE_GROCERY(1004),
    SHOP_TYPE_FOOD(1005),
    SHOP_TYPE_SEA_LAMP(1006),
    SHOP_TYPE_VIRTUAL_SHOP(1007),
    SHOP_TYPE_LIYUE_GROCERY(1008),
    SHOP_TYPE_LIYUE_SOUVENIR(1009),
    SHOP_TYPE_LIYUE_RESTAURANT(1010),
    SHOP_TYPE_NPC_Flora(1011), //prob
    SHOP_TYPE_NPC_Charles(1012), //prob
    SHOP_TYPE_NPC_Shiliu(1013), //prob
    SHOP_TYPE_NPC_Schulz(1014), //prob
    SHOP_TYPE_NPC_Brook(1015), //prob
    SHOP_TYPE_NPC_Hopkins(1016), //prob
    SHOP_TYPE_NPC_Draff(1017), //prob
    SHOP_TYPE_NPC_Chloris(1018), //prob
    SHOP_TYPE_NPC_Licai(1019), //prob
    SHOP_TYPE_NPC_Yueshu(1020), //prob
    SHOP_TYPE_NPC_Gui(1021), //prob
    SHOP_TYPE_NPC_Gao(1022), //prob
    SHOP_TYPE_NPC_Sun(1023), //prob
    SHOP_TYPE_NPC_Qiming(1024), //prob
    SHOP_TYPE_NPC_Zhangshun(1025), //prob
    SHOP_TYPE_NPC_Chen(1026), //prob
    SHOP_TYPE_NPC_ErNiang(1027), //prob
    SHOP_TYPE_NPC_Shitou(1028), //prob
    SHOP_TYPE_NPC_Jifang(1029), //prob
    SHOP_TYPE_NPC_Zhu(1030), //prob
    SHOP_TYPE_NPC_Bai(1031), //prob
    SHOP_TYPE_NPC_Kai(1032), //prob
    SHOP_TYPE_NPC_Linglang(1033), //prob
    SHOP_TYPE_NPC_VerrGoldet(1034), //prob
    SHOP_TYPE_NPC_Zhou(1035), //prob
    SHOP_TYPE_TASK_Ekaterina(1036), //prob
    SHOP_TYPE_ACTIVITY_ASTER(1037),
    SHOP_TYPE_TASK_Tartaglia(1038), //prob
    SHOP_TYPE_NPC_Harris(1039), //prob
    SHOP_TYPE_ACTIVITY_DRAGON_SPINE(1040),
    SHOP_TYPE_ACTIVITY_TREASURE_MAP(1041), //prob
    SHOP_TYPE_NPC_Yinian(1042), //prob
    SHOP_TYPE_ACTIVITY_SEA_LAMP(1043), //event guess
    SHOP_TYPE_ACTIVITY_FLEUR_FAIR(1044),
    SHOP_TYPE_NPC_Changshun(1045), //prob
    SHOP_TYPE_NPC_Bolai(1046), //prob
    SHOP_TYPE_NPC_Ashanpo(1047), //prob
    SHOP_TYPE_HOME(1048),
    SHOP_TYPE_HOME_LIMIT(1049), // prob
    SHOP_TYPE_NPC_MasterLu(1050), //prob
    SHOP_TYPE_NPC_Goth(1051), //prob
    SHOP_TYPE_COSTUME(1052),
    SHOP_TYPE_NPC_Obata(1053), //prob
    SHOP_TYPE_NPC_Qiuyue(1054), //prob
    SHOP_TYPE_NPC_Ryouko(1055), //prob
    SHOP_TYPE_INAZUMA_GROCERY(1056), //prob
    SHOP_TYPE_INAZUMA_SOUVENIR(1057), //prob
    SHOP_TYPE_INAZUMA_RESTAURANT(1058), //prob
    SHOP_TYPE_NPC_Kuroda(1059), //prob
    SHOP_TYPE_NPC_KiminamiAnna(1060), //prob
    SHOP_TYPE_NPC_Tomoki(1061), //prob
    SHOP_TYPE_NPC_Karpillia(1062), //prob
    SHOP_TYPE_BLACKSMITH_INAZUMA(1063), //prob
    SHOP_TYPE_FISH(1064), //prob
    SHOP_TYPE_FISH_LIYUE(1065), //prob
    SHOP_TYPE_FISH_INAZUMA(1066), //prob
    SHOP_TYPE_NPC_Kiyoko(1067), //prob
    SHOP_TYPE_EXPIRED_WIDGET_MENGDE(1068), //prob
    SHOP_TYPE_CAPTURE_ANIMAL_SHOP(1069), //prob
    SHOP_TYPE_NPC_YamashiroKenta(1070), //prob
    SHOP_TYPE_ACTIVITY_CHANNELLER_SLAB(15001), //prob
    SHOP_TYPE_ACTIVITY_SUMMER_TIME(16001),
    SHOP_TYPE_ACTIVITY_BOUNCE_CONJURING(16002),
    SHOP_TYPE_ACTIVITY_BLITZ_RUSH(20001),//name?
    SHOP_TYPE_ACTIVITY_CHESS(20002),//name?
    SHOP_TYPE_ACTIVITY_WINTER_CAMP(20004),
    SHOP_TYPE_ACTIVITY_LANTERN_RITE(20005),//name?
    SHOP_TYPE_ACTIVITY_ROGUELIKE_DUNGEON(22001),// maybe
    SHOP_TYPE_ACTIVITY_ROGUE_DIARY(27001),//name?
    SHOP_TYPE_ACTIVITY_SUMMER_TIME_V2(28001), // guess
    SHOP_TYPE_ACTIVITY_GRAVEN_INNOCENCE(30001); // guess

    public final int shopTypeId;

    ShopType(int shopTypeId){
        this.shopTypeId = shopTypeId;
    }

    public static ShopType getById(int shopTypeId){
        for(ShopType type : ShopType.values()){
            if(type.shopTypeId == shopTypeId) return type;
        }
        return SHOP_TYPE_NONE;
    }
}
