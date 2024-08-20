package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.home.SetFriendEnterHomeOptionRsp;

public class PacketSetFriendEnterHomeOptionRsp extends BaseTypedPacket<SetFriendEnterHomeOptionRsp> {
    public PacketSetFriendEnterHomeOptionRsp() {
        super(new SetFriendEnterHomeOptionRsp());
    }
}
