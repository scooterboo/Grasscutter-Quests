package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetOnlinePlayerListRsp;
import messages.chat.GetOnlinePlayerListReq;

public class HandlerGetOnlinePlayerListReq extends TypedPacketHandler<GetOnlinePlayerListReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetOnlinePlayerListReq req) throws Exception {
        session.send(new PacketGetOnlinePlayerListRsp(session.getPlayer()));
    }
}
