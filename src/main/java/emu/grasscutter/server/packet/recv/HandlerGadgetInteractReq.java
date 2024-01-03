package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import messages.gadget.GadgetInteractReq;

public class HandlerGadgetInteractReq extends TypedPacketHandler<GadgetInteractReq> {

	@Override
	public void handle(GameSession session, byte[] header, GadgetInteractReq req) throws Exception {
        session.getPlayer().getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_INTERACT_GADGET, req.getGadgetId());
		session.getPlayer().interactWith(req.getGadgetEntityId(), req);
	}

}
