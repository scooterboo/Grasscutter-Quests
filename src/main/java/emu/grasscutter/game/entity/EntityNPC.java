package emu.grasscutter.game.entity;

import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.*;
import emu.grasscutter.scripts.data.SceneNPC;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lombok.Getter;
import lombok.val;
import messages.general.Vector;
import messages.general.ability.AbilitySyncStateInfo;
import messages.scene.entity.*;

import java.util.List;

public class EntityNPC extends GameEntity {
    @Getter(onMethod = @__(@Override))
    private final Position position;
    @Getter(onMethod = @__(@Override))
    private final Position rotation;
    private final SceneNPC metaNpc;
    @Getter private final int suiteId;

    public EntityNPC(Scene scene, SceneNPC metaNPC, int blockId, int suiteId) {
        super(scene);
        this.id = getScene().getWorld().getNextEntityId(EntityIdType.NPC);
        setConfigId(metaNPC.getConfig_id());
        setGroupId(metaNPC.getGroup().getId());
        setBlockId(blockId);
        this.suiteId = suiteId;
        this.position = metaNPC.getPos().clone();
        this.rotation = metaNPC.getRot().clone();
        this.metaNpc = metaNPC;

    }

    @Override
    public int getEntityTypeId() {
        return metaNpc.getNpc_id();
    }

    @Override
    public Int2FloatMap getFightProperties() {
        return null;
    }

    @Override
    public SceneEntityInfo toProto() {
        val protoBornPos = getPosition().toProto();
        val protoPos = getPosition().toProto();
        val protoRot = getRotation().toProto();
        val aiInfo = new SceneEntityAiInfo(true, protoBornPos);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, protoBornPos);

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_NPC, getId());
        entityInfo.setMotionInfo(new MotionInfo(protoPos, protoRot, new Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);


        val npc = new SceneNpcInfo(metaNpc.getNpc_id());
        npc.setBlockId(getBlockId());
        entityInfo.setEntity(new SceneEntityInfo.Entity.Npc(npc));

        return entityInfo;
    }
}
