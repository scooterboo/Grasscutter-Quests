package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import messages.chat.PullPrivateChatReq;

public class HandlerPullPrivateChatReq extends TypedPacketHandler<PullPrivateChatReq> {

    @Override
    public void handle(GameSession session, byte[] header, PullPrivateChatReq req) throws Exception {
        session.getServer().getChatSystem().handlePullPrivateChatReq(session.getPlayer(), req.getTargetUid());

        // session.send(new PacketPullPrivateChatRsp(req.getTargetUid()));
    }

}
