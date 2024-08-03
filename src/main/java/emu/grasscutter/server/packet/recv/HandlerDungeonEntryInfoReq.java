package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonEntryInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonEntryInfoReq;

public class HandlerDungeonEntryInfoReq extends TypedPacketHandler<DungeonEntryInfoReq> {

    @Override
    public void handle(GameSession session, byte[] header, DungeonEntryInfoReq req) throws Exception {
        session.getPlayer().sendPacket(new PacketDungeonEntryInfoRsp(session.getPlayer(), req.getSceneId(), req.getPointId()));
    }
}
