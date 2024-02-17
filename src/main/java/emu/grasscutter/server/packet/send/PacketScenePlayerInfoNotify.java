package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import messages.scene.ScenePlayerInfo;
import messages.scene.ScenePlayerInfoNotify;

public class PacketScenePlayerInfoNotify extends BaseTypedPacket<ScenePlayerInfoNotify> {

	public PacketScenePlayerInfoNotify(World world) {
		super(new ScenePlayerInfoNotify());

        val playerInfoList = world.getPlayers().stream().map(p -> {
            val pInfo = new ScenePlayerInfo(p.getUid(), p.getPeerId(), p.getNickname());
            pInfo.setSceneId(p.getSceneId());
            pInfo.setOnlinePlayerInfo(p.getOnlinePlayerInfo());
            return pInfo;
        }).toList();
		proto.setPlayerInfoList(playerInfoList);

	}
}
