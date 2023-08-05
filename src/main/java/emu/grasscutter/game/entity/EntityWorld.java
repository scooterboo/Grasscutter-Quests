package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.interfaces.StringAbilityEntity;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.game.world.World;
import emu.grasscutter.net.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatArrayMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;

import java.util.Collection;

public class EntityWorld extends MetaGameEntity implements StringAbilityEntity {

    private World world;

    public EntityWorld(World world) {
        super(null);
        this.world = world;

        this.id = world.getNextEntityId(EntityIdType.MPLEVEL);
        initAbilities();
    }

    @Override
    public Scene getScene() {
        return world.getHost().getScene();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public AbilityManager getAbilityTargetManager() {
        return world.getHost().getAbilityManager();
    }

    @Override
    public Collection<String> getAbilityData() {
        return GameData.getConfigGlobalCombat().getDefaultAbilities().getDefaultMPLevelAbilities();
    }

    @Override
    public int getEntityTypeId() {
        return EntityIdType.TEAM.getId();
    }

}
