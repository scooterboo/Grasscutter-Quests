package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.net.proto.PacketHeadOuterClass.PacketHead;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEnterWorldAreaRsp;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.first.EnterWorldAreaReq;

public class HandlerEnterWorldAreaReq extends TypedPacketHandler<EnterWorldAreaReq> {
    @Override
    public void handle(GameSession session, byte[] header, EnterWorldAreaReq req) throws Exception {
		PacketHead head = PacketHead.parseFrom(header);
        session.send(new PacketEnterWorldAreaRsp(head.getClientSequenceId(), req));
		//session.send(new PacketScenePlayerLocationNotify(session.getPlayer()));
	}
}
