package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.SetPlayerPropRsp;

public class PacketSetPlayerPropRsp extends BaseTypedPacket<SetPlayerPropRsp> {
	public PacketSetPlayerPropRsp(int retCode) {
        super(new SetPlayerPropRsp());
			proto.setRetcode(retCode);
    }
}
