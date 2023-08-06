package emu.grasscutter.game.entity;

import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.SceneEntityInfoOuterClass;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;

public abstract class MetaGameEntity extends GameEntity {
    public MetaGameEntity(Scene scene) {
        super(scene);
    }

    @Override
    public Position getPosition() {
        return new Position(0, 0, 0);
    }

    @Override
    public Position getRotation(){
        return new Position(0, 0, 0);
    }

    @Override
    public Int2FloatMap getFightProperties() {
        return null;
    }

    @Override
    public SceneEntityInfoOuterClass.SceneEntityInfo toProto() {
        return null;
    }
}
