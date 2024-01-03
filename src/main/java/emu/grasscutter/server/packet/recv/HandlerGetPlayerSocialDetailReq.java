package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetPlayerSocialDetailRsp;
import messages.chat.GetPlayerSocialDetailReq;
import messages.chat.SocialDetail;

public class HandlerGetPlayerSocialDetailReq extends TypedPacketHandler<GetPlayerSocialDetailReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetPlayerSocialDetailReq req) throws Exception {
		SocialDetail detail = session.getServer().getSocialDetailByUid(req.getUid());

		if (detail != null) {
			detail.setFriend(session.getPlayer().getFriendsList().isFriendsWith(req.getUid()));
		}

		session.send(new PacketGetPlayerSocialDetailRsp(detail));
	}
}
