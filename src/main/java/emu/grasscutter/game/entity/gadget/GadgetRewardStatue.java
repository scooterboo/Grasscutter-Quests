package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketGadgetInteractRsp;
import lombok.val;
import messages.gadget.GadgetInteractReq;
import messages.gadget.InterOpType;
import messages.gadget.InteractType;
import messages.gadget.ResinCostType;
import messages.scene.entity.SceneGadgetInfo;
import messages.scene.entity.StatueGadgetInfo;

import java.util.Optional;

public class GadgetRewardStatue extends GadgetContent {

    public GadgetRewardStatue(EntityGadget gadget) {
        super(gadget);
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        if (req.getOpType() == InterOpType.INTER_OP_FINISH) {
            val useCondense = req.getResinCostType() == ResinCostType.RESIN_COST_TYPE_CONDENSE;
            Optional.ofNullable(player.getScene().getDungeonManager()).ifPresent(m ->
                m.getStatueDrops(player, useCondense, getGadget().getGroupId()));
        }

        player.sendPacket(new PacketGadgetInteractRsp(getGadget(), InteractType.INTERACT_OPEN_STATUE, req.getOpType()));
        return false;
    }

    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        val proto = new StatueGadgetInfo();
        Optional.ofNullable(getGadget().getScene().getDungeonManager())
            .ifPresent(m -> proto.setOpenedStatueUidList(m.getRewardedPlayers().stream().toList()));

        gadgetInfo.setContent(new SceneGadgetInfo.Content.StatueGadget(proto));
    }
}
