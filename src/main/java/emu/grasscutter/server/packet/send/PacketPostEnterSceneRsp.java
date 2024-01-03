package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.PostEnterSceneRsp;

public class PacketPostEnterSceneRsp extends BaseTypedPacket<PostEnterSceneRsp> {

	public PacketPostEnterSceneRsp(Player player) {
		super(new PostEnterSceneRsp(player.getEnterSceneToken()));
	}
}
