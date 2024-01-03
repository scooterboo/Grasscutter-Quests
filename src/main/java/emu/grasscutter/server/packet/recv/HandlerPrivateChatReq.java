package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import messages.chat.PrivateChatReq;

public class HandlerPrivateChatReq extends TypedPacketHandler<PrivateChatReq> {

    @Override
    public void handle(GameSession session, byte[] header, PrivateChatReq req) throws Exception {
        val content = req.getContent();

        if (content instanceof PrivateChatReq.Content.Text text) {
            session.getServer().getChatSystem().sendPrivateMessage(session.getPlayer(), req.getTargetUid(), text.getValue());
        } else if (content instanceof PrivateChatReq.Content.Icon icon) {
            session.getServer().getChatSystem().sendPrivateMessage(session.getPlayer(), req.getTargetUid(), icon.getValue());
        }
    }

}
