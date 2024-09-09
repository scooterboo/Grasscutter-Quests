package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.net.proto.PacketHeadOuterClass.PacketHead;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPathfindingEnterSceneRsp;
import org.anime_game_servers.multi_proto.gi.messages.pathfinding.PathfindingEnterSceneReq;

public class HandlerPathfindingEnterSceneReq extends TypedPacketHandler<PathfindingEnterSceneReq> {
	@Override
    public void handle(GameSession session, byte[] header, PathfindingEnterSceneReq req) throws Exception {
		PacketHead head = PacketHead.parseFrom(header);
		session.send(new PacketPathfindingEnterSceneRsp(head.getClientSequenceId()));
	}
}
