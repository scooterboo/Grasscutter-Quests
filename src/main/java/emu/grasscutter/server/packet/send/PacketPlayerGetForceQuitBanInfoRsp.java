package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.PlayerGetForceQuitBanInfoRsp;

public class PacketPlayerGetForceQuitBanInfoRsp extends BaseTypedPacket<PlayerGetForceQuitBanInfoRsp> {
	public PacketPlayerGetForceQuitBanInfoRsp(int retcode) {
        super(new PlayerGetForceQuitBanInfoRsp());
        proto.setRetcode(retcode);
	}
}
