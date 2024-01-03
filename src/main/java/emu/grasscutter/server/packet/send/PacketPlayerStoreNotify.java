package emu.grasscutter.server.packet.send;

import static emu.grasscutter.config.Configuration.*;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.storage.PlayerStoreNotify;

public class PacketPlayerStoreNotify extends BaseTypedPacket<PlayerStoreNotify> {

    public PacketPlayerStoreNotify(Player player) {
        super(new PlayerStoreNotify(messages.storage.StoreType.STORE_PACK));

        this.buildHeader(2);
        proto.setWeightLimit(GAME_OPTIONS.inventoryLimits.all);

        proto.setItemList(player.getInventory().stream().map(GameItem::toProto).toList());
    }
}
