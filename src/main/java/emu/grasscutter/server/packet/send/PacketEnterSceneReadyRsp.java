package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.scene.EnterSceneReadyRsp;

public class PacketEnterSceneReadyRsp extends BaseTypedPackage<EnterSceneReadyRsp> {

	public PacketEnterSceneReadyRsp(Player player) {
		super(new EnterSceneReadyRsp(player.getEnterSceneToken()), 11);
	}
}
