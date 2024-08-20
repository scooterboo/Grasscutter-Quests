package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.home.FurnitureMakeStartReq;

public class HandlerFurnitureMakeStartReq extends TypedPacketHandler<FurnitureMakeStartReq> {
	@Override
    public void handle(GameSession session, byte[] header, FurnitureMakeStartReq req) throws Exception {
		session.getPlayer().getFurnitureManager().startMake(req.getMakeId(), req.getAvatarId());
	}
}
