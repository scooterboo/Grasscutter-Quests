package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.multiplayer.PlayerApplyEnterMpNotify;

public class PacketPlayerApplyEnterMpNotify extends BaseTypedPacket<PlayerApplyEnterMpNotify> {

	public PacketPlayerApplyEnterMpNotify(Player srcPlayer) {
		super(new PlayerApplyEnterMpNotify(srcPlayer.getOnlinePlayerInfo()));
	}
}
