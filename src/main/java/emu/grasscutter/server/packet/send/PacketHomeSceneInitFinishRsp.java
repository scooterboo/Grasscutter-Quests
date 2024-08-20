package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.home.HomeSceneInitFinishRsp;

public class PacketHomeSceneInitFinishRsp extends BaseTypedPacket<HomeSceneInitFinishRsp> {
	public PacketHomeSceneInitFinishRsp() {
        super(new HomeSceneInitFinishRsp());
	}
}
