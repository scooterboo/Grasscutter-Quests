package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCancelCoopTaskRsp;
import lombok.val;
import messages.coop.CancelCoopTaskReq;

public class HandlerCancelCoopTaskReq extends TypedPacketHandler<CancelCoopTaskReq> {

	@Override
	public void handle(GameSession session, byte[] header, CancelCoopTaskReq req) throws Exception {

		//TODO: smarter quest clearing that actually survives a relog
		//I don't have enough implemented, so I just delete everything multiple times.
		GameData.getCoopPointDataMap().values().stream()
				.filter(x -> x.getChapterId() == req.getChapterId()).forEach(x -> {
					val quest = session.getPlayer().getQuestManager().getQuestById(x.getAcceptQuest());
					if (quest != null)
						quest.getMainQuest().getChildQuests().values().stream().filter(p -> p.getQuestData().getOrder() >= quest.getQuestData().getOrder()).forEach(q -> {
							q.clearProgress(true);
						});
				});
		session.send(new PacketCancelCoopTaskRsp(req.getChapterId()));
	}

}