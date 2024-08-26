package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.SceneKickPlayerRsp;

public class PacketSceneKickPlayerRsp extends BaseTypedPacket<SceneKickPlayerRsp> {
	public PacketSceneKickPlayerRsp(int targetUid) {
        super(new SceneKickPlayerRsp());
        proto.setTargetUid(targetUid);
	}

	public PacketSceneKickPlayerRsp() {
        super(new SceneKickPlayerRsp());
        proto.setRetcode(RetcodeOuterClass.Retcode.RET_SVR_ERROR_VALUE);
	}
}
