package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.home.HomeUpdateArrangementInfoRsp;

public class PacketHomeUpdateArrangementInfoRsp extends BaseTypedPacket<HomeUpdateArrangementInfoRsp> {
	public PacketHomeUpdateArrangementInfoRsp() {
        super(new HomeUpdateArrangementInfoRsp());
	}
}
