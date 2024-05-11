package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCoopChapterUpdateNotify;
import emu.grasscutter.server.packet.send.PacketCoopDataNotify;
import emu.grasscutter.server.packet.send.PacketUnlockCoopChapterRsp;
import lombok.val;
import messages.coop.CoopPointState;
import messages.coop.UnlockCoopChapterReq;

public class HandlerUnlockCoopChapterReq extends TypedPacketHandler<UnlockCoopChapterReq> {

	@Override
	public void handle(GameSession session, byte[] header, UnlockCoopChapterReq req) throws Exception {
		//take keys
		session.getPlayer().useLegendaryKey(2);

		//update database then send PacketCoopChapterUpdateNotify
		session.getPlayer().getCoopHandler().unlockChapterUpdateNotify(req.getChapterId());

		//send PacketUnlockCoopChapterRsp
		session.send(new PacketUnlockCoopChapterRsp(req.getChapterId()));
	}

}