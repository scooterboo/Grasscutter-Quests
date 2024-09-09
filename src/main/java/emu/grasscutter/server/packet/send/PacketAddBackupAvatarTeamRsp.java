package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.AddBackupAvatarTeamRsp;

public class PacketAddBackupAvatarTeamRsp extends BaseTypedPacket<AddBackupAvatarTeamRsp> {
    public PacketAddBackupAvatarTeamRsp(Retcode retcode) {
        super(new AddBackupAvatarTeamRsp());
        proto.setRetcode(retcode.getNumber());
    }

    public PacketAddBackupAvatarTeamRsp() {
        this(Retcode.RET_SUCC);
    }
}
