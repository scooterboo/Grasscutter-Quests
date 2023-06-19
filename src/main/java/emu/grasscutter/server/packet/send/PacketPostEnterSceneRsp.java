package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.scene.PostEnterSceneRsp;

public class PacketPostEnterSceneRsp extends BaseTypedPackage<PostEnterSceneRsp> {

	public PacketPostEnterSceneRsp(Player player) {
		super(new PostEnterSceneRsp(player.getEnterSceneToken()));
	}
}
