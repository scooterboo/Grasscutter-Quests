package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.home.HomeChooseModuleRsp;

public class PacketHomeChooseModuleRsp extends BaseTypedPacket<HomeChooseModuleRsp> {
    public PacketHomeChooseModuleRsp(int moduleId) {
        super(new HomeChooseModuleRsp());
        proto.setRetcode(0);
        proto.setModuleId(moduleId);
    }
}
