package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.SceneInitFinishRsp;

public class PacketSceneInitFinishRsp extends BaseTypedPacket<SceneInitFinishRsp> {

	public PacketSceneInitFinishRsp(Player player) {
		super(new SceneInitFinishRsp(player.getEnterSceneToken()), 11);
	}
}
