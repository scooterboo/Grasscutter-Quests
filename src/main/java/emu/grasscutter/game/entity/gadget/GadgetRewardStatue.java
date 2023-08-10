package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.GadgetInteractReqOuterClass.GadgetInteractReq;
import emu.grasscutter.net.proto.InterOpTypeOuterClass.InterOpType;
import emu.grasscutter.net.proto.InteractTypeOuterClass.InteractType;
import emu.grasscutter.net.proto.SceneGadgetInfoOuterClass.SceneGadgetInfo;
import emu.grasscutter.net.proto.StatueGadgetInfoOuterClass.StatueGadgetInfo;
import emu.grasscutter.server.packet.send.PacketGadgetInteractRsp;
import lombok.val;

import static emu.grasscutter.net.proto.ResinCostTypeOuterClass.ResinCostType.RESIN_COST_TYPE_CONDENSE;

import java.util.Optional;

public class GadgetRewardStatue extends GadgetContent {

    public GadgetRewardStatue(EntityGadget gadget) {
        super(gadget);
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        if (req.getOpType() == InterOpType.INTER_OP_TYPE_FINISH) {
            val useCondense = req.getResinCostType() == RESIN_COST_TYPE_CONDENSE;
            Optional.ofNullable(player.getScene().getDungeonManager()).ifPresent(m ->
                m.getStatueDrops(player, useCondense, getGadget().getGroupId()));
        }

        player.sendPacket(new PacketGadgetInteractRsp(getGadget(), InteractType.INTERACT_TYPE_OPEN_STATUE, req.getOpType()));
        return false;
    }

    public void onBuildProto(SceneGadgetInfo.Builder gadgetInfo) {
        val proto = StatueGadgetInfo.newBuilder();
        Optional.ofNullable(getGadget().getScene().getDungeonManager())
            .ifPresent(m -> proto.addAllOpenedStatueUidList(m.getRewardedPlayers()));

        gadgetInfo.setStatueGadget(proto.build());
    }
}
