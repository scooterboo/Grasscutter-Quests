package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import messages.player.SetOpenStateReq;

public class HandlerSetOpenStateReq extends TypedPacketHandler<SetOpenStateReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetOpenStateReq req) throws Exception {
        int openState = req.getKey();
        int value = req.getValue();

        session.getPlayer().getProgressManager().setOpenStateFromClient(openState, value);
    }
}
