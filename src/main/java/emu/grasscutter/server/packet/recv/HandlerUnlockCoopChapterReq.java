package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player.CoopCardEntry;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCoopDataNotify;
import emu.grasscutter.server.packet.send.PacketUnlockCoopChapterRsp;
import lombok.val;
import messages.coop.CoopPointState;
import messages.coop.UnlockCoopChapterReq;

public class HandlerUnlockCoopChapterReq extends TypedPacketHandler<UnlockCoopChapterReq> {

	@Override
	public void handle(GameSession session, byte[] header, UnlockCoopChapterReq req) throws Exception {
		val coopCard = session.getPlayer().getCoopCards()
				.computeIfAbsent(req.getChapterId(), v -> new CoopCardEntry(req.getChapterId()));

		//set card as accepted
		coopCard.setAccepted(true);

		//set first point (POINT_START) to STATE_STARTED
		val startPointId = GameData.getCoopPointDataMap().values().stream()
				.filter(j -> j.getChapterId() == req.getChapterId() && j.getPointPosId() == 1)
				.toList()
				.get(0)
				.getId();
		coopCard.getPoints()
				.get(startPointId)
				.setState(CoopPointState.STATE_STARTED);

		//take keys
		session.getPlayer().useLegendaryKey(2);

		//TODO: verify this packet gets sent here.
		session.send(new PacketCoopDataNotify(session.getPlayer()));

		session.send(new PacketUnlockCoopChapterRsp(req.getChapterId()));
	}

}