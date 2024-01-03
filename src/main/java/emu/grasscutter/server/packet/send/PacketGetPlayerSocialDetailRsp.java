package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass;
import messages.chat.GetPlayerSocialDetailRsp;
import messages.chat.SocialDetail;

public class PacketGetPlayerSocialDetailRsp extends BaseTypedPacket<GetPlayerSocialDetailRsp> {

	public PacketGetPlayerSocialDetailRsp(SocialDetail detail) {
		super(new GetPlayerSocialDetailRsp());

		if (detail != null) {
			proto.setDetailData(detail);
		} else {
			proto.setRetCode(RetcodeOuterClass.Retcode.RET_SVR_ERROR_VALUE);
		}
	}
}
