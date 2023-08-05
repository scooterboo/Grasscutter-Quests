package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.interfaces.StringAbilityEntity;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatArrayMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;

import java.util.Collection;

public class EntityScene extends GameEntity implements StringAbilityEntity {

    public EntityScene(Scene scene) {
        super(scene);
        initAbilities();
    }

    @Override
    public AbilityManager getAbilityTargetManager() {
        return getScene().getWorld().getHost().getAbilityManager();
    }

    @Override
    public Collection<String> getAbilityData() {
        //Load abilities from levelElementAbilities
        return GameData.getConfigGlobalCombat().getDefaultAbilities().getLevelElementAbilities();
    }

    @Override
    public int getEntityTypeId() {
        return 0x13;
    }

    @Override
    public Int2FloatMap getFightProperties() {
        //TODO
        return new Int2FloatArrayMap();
    }

    @Override
    public Position getPosition() {
        // TODO Auto-generated method stub
        return new Position(0, 0, 0);
    }

    @Override
    public Position getRotation() {
        return new Position(0, 0, 0);
    }

    @Override
    public SceneEntityInfo toProto() {
        return null;
    }

}
