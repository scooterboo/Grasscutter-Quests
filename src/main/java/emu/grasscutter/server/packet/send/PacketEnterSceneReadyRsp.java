package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterSceneReadyRsp;

public class PacketEnterSceneReadyRsp extends BaseTypedPacket<EnterSceneReadyRsp> {

	public PacketEnterSceneReadyRsp(Player player) {
        super(new EnterSceneReadyRsp(), 11);
        proto.setEnterSceneToken(player.getEnterSceneToken());
	}
}
