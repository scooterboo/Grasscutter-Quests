package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.SyncScenePlayTeamEntityNotify;

public class PacketSyncScenePlayTeamEntityNotify extends BaseTypedPacket<SyncScenePlayTeamEntityNotify> {

	public PacketSyncScenePlayTeamEntityNotify(Player player) {
		super(new SyncScenePlayTeamEntityNotify(player.getSceneId()));
	}
}
