package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.scene.EnterScenePeerNotify;

public class PacketEnterScenePeerNotify extends BaseTypedPackage<EnterScenePeerNotify> {

	public PacketEnterScenePeerNotify(Player player) {
		super(new EnterScenePeerNotify(
            player.getSceneId(),
            player.getPeerId(),
            player.getWorld().getHost().getPeerId(),
            player.getEnterSceneToken())
        );
	}
}
