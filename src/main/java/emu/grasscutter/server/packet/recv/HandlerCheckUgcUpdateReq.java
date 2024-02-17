package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCheckUgcUpdateRsp;
import messages.activity.user_generated_content.music_game.CheckUgcUpdateReq;

public class HandlerCheckUgcUpdateReq extends TypedPacketHandler<CheckUgcUpdateReq> {

    @Override
    public void handle(GameSession session, byte[] header, CheckUgcUpdateReq payload) throws Exception {
        session.send(new PacketCheckUgcUpdateRsp(payload.getUgcType()));
    }

}
