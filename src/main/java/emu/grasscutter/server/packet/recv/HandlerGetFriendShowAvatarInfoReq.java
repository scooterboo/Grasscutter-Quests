package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetFriendShowAvatarInfoRsp;
import messages.chat.GetFriendShowAvatarInfoReq;

public class HandlerGetFriendShowAvatarInfoReq extends TypedPacketHandler<GetFriendShowAvatarInfoReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetFriendShowAvatarInfoReq req) throws Exception {

		int targetUid = req.getUid();
		Player targetPlayer = session.getServer().getPlayerByUid(targetUid, true);

		if (targetPlayer.isShowAvatars()) {
			session.send(new PacketGetFriendShowAvatarInfoRsp(targetUid, targetPlayer.getShowAvatarInfoList()));
		}
	}

}
