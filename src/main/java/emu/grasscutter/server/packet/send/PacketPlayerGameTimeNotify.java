package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.player.PlayerGameTimeNotify;

public class PacketPlayerGameTimeNotify extends BaseTypedPacket<PlayerGameTimeNotify> {

	public PacketPlayerGameTimeNotify(Player player) {
		super(new PlayerGameTimeNotify(player.getUid(), player.getWorld().getGameTime()));
	}
}
