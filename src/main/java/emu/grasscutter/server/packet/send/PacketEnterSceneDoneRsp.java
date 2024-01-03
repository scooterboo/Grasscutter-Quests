package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.EnterSceneDoneRsp;

public class PacketEnterSceneDoneRsp extends BaseTypedPacket<EnterSceneDoneRsp> {

	public PacketEnterSceneDoneRsp(Player player) {
		super(new EnterSceneDoneRsp(player.getEnterSceneToken()));
	}
}
