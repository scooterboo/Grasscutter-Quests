package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass;
import org.anime_game_servers.multi_proto.gi.messages.home.TryEnterHomeRsp;


public class PacketTryEnterHomeRsp extends BaseTypedPacket<TryEnterHomeRsp> {

    public PacketTryEnterHomeRsp() {
        super(new TryEnterHomeRsp());
        proto.setRetcode(RetcodeOuterClass.Retcode.RET_SVR_ERROR_VALUE);
    }

    public PacketTryEnterHomeRsp(int uid) {
        this(0, uid);
    }

    public PacketTryEnterHomeRsp(int retCode, int uid) {
        super(new TryEnterHomeRsp());
        proto.setRetcode(retCode);
        proto.setTargetUid(uid);
    }
}
