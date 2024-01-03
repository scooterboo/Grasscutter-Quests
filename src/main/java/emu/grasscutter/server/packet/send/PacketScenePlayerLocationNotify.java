package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.ScenePlayerLocationNotify;

public class PacketScenePlayerLocationNotify extends BaseTypedPacket<ScenePlayerLocationNotify> {

	public PacketScenePlayerLocationNotify(Scene scene) {
		super(new ScenePlayerLocationNotify(scene.getId()));

        proto.setPlayerLocList(scene.getPlayers().stream().map(Player::getPlayerLocationInfo).toList());
	}
}
