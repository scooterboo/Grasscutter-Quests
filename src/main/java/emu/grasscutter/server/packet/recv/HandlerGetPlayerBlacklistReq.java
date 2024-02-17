package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import messages.chat.GetPlayerBlacklistReq;
import messages.chat.GetPlayerBlacklistRsp;

public class HandlerGetPlayerBlacklistReq extends TypedPacketHandler<GetPlayerBlacklistReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetPlayerBlacklistReq payload) throws Exception {
        val rsp = new BaseTypedPacket<>(new GetPlayerBlacklistRsp()){};
		session.send(rsp.buildHeader(3));
	}

}
