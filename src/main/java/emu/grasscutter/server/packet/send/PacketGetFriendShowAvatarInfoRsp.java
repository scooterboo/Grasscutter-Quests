package emu.grasscutter.server.packet.send;

import java.util.List;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.chat.GetFriendShowAvatarInfoRsp;
import messages.general.avatar.ShowAvatarInfo;

public class PacketGetFriendShowAvatarInfoRsp extends BaseTypedPacket<GetFriendShowAvatarInfoRsp> {

	public PacketGetFriendShowAvatarInfoRsp(int uid, List<ShowAvatarInfo> showAvatarInfoList) {
		super(new GetFriendShowAvatarInfoRsp(uid, showAvatarInfoList));
	}

}
