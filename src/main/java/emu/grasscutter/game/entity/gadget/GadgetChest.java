package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.dungeons.DungeonManager;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.chest.BossChestInteractHandler;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.scripts.constants.ScriptGadgetState;
import emu.grasscutter.server.packet.send.PacketGadgetInteractRsp;
import lombok.val;
import messages.gadget.GadgetInteractReq;
import messages.gadget.InterOpType;
import messages.gadget.InteractType;
import messages.gadget.ResinCostType;
import messages.scene.entity.BossChestInfo;
import messages.scene.entity.SceneGadgetInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class GadgetChest extends GadgetContent {

    public GadgetChest(EntityGadget gadget) {
        super(gadget);
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        val chestInteractHandlerMap = getGadget().getScene().getWorld().getServer().getWorldDataSystem().getChestInteractHandlerMap();
        val handler = chestInteractHandlerMap.get(getGadget().getGadgetData().getJsonName());
        if (handler == null) {
            Grasscutter.getLogger().warn("Could not found the handler of this type of Chests {}", getGadget().getGadgetData().getJsonName());
            return false;
        }

        if (req.getOpType() == InterOpType.INTER_OP_START && handler.isTwoStep()) {
            player.sendPacket(new PacketGadgetInteractRsp(getGadget(), InteractType.INTERACT_OPEN_CHEST, InterOpType.INTER_OP_START));
            return false;
        } else {
            boolean success;
            if (handler instanceof BossChestInteractHandler bossChestInteractHandler) {
                success = bossChestInteractHandler.onInteract(this, player,
                    req.getResinCostType() == ResinCostType.RESIN_COST_TYPE_CONDENSE);
            } else {
                success = handler.onInteract(this, player);
            }
            if (!success) return false;

            getGadget().updateState(ScriptGadgetState.ChestOpened);
            player.sendPacket(new PacketGadgetInteractRsp(this.getGadget(), InteractType.INTERACT_OPEN_CHEST));
            return true;
        }
    }

    /**
     * Builds proto information for gadgets, note that
     * it will override the gadget's properties even if the builder is empty
     * */
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        val playersUid = getGadget().getScene().getPlayers().stream().map(Player::getUid).toList();

        Optional.ofNullable(getGadget().getMetaGadget())
            .map(g -> g.getBoss_chest())
            .ifPresent(bossChest -> {
                val chestProto = new BossChestInfo(bossChest.getMonster_config_id(), bossChest.getResin());

                // removing instead of creating new list directly below is because
                // it also has to consider normal cases
                val qualifiedUids = new ArrayList<>(playersUid);
                // don't allow player to take again if he has taken weekly boss already
                val dungeonManager = getGadget().getScene().getDungeonManager();
                if(dungeonManager != null){
                   val weeklyInfo = dungeonManager.getWeeklyBossUidInfo();
                   chestProto.getUidDiscountMap().putAll(weeklyInfo);
                   qualifiedUids.retainAll(weeklyInfo.keySet());
                }

                chestProto.setQualifyUidList(playersUid);
                chestProto.setRemainUidList(qualifiedUids);
                gadgetInfo.setContent(new SceneGadgetInfo.Content.BossChest(chestProto));
            });


        Optional.ofNullable(getGadget().getScene().getWorld().getHost().getBlossomManager()
                .getChestInfo(getGadget().getConfigId(), playersUid))
            .ifPresent(chest -> gadgetInfo.setContent(new SceneGadgetInfo.Content.BlossomChest(chest)));
    }
}
