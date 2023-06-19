package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.scene.SceneInitFinishRsp;

public class PacketSceneInitFinishRsp extends BaseTypedPackage<SceneInitFinishRsp> {

	public PacketSceneInitFinishRsp(Player player) {
		super(new SceneInitFinishRsp(player.getEnterSceneToken()), 11);
	}
}
