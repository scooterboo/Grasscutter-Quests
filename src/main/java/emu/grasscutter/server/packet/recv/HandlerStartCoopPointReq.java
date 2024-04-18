package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketStartCoopPointRsp;
import lombok.val;
import messages.coop.StartCoopPointReq;

public class HandlerStartCoopPointReq extends TypedPacketHandler<StartCoopPointReq> {

	@Override
	public void handle(GameSession session, byte[] header, StartCoopPointReq req) throws Exception {
		val coopPoint = GameData.getCoopPointDataMap().values().stream()
				.filter(x -> x.getId() == req.getCoopPoint())
				.toList();
		if(!coopPoint.isEmpty()) {
			val chapterId = coopPoint.get(0).getChapterId();
			val acceptQuest = coopPoint.get(0).getAcceptQuest();
			session.getPlayer().getQuestManager().queueEvent(QuestCond.QUEST_COND_MAIN_COOP_START, chapterId, acceptQuest);
		}
		session.send(new PacketStartCoopPointRsp(req.getCoopPoint()));
	}

}