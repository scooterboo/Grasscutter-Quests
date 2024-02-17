package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.EnterSceneReadyRsp;

public class PacketEnterSceneReadyRsp extends BaseTypedPacket<EnterSceneReadyRsp> {

	public PacketEnterSceneReadyRsp(Player player) {
		super(new EnterSceneReadyRsp(player.getEnterSceneToken()), 11);
	}
}
