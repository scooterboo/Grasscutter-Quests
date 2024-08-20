package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.tower.TowerEnterLevelReq;

public class HandlerTowerEnterLevelReq extends TypedPacketHandler<TowerEnterLevelReq> {
    @Override
    public void handle(GameSession session, byte[] header, TowerEnterLevelReq req) throws Exception {
        session.getPlayer().getTowerManager().enterLevel(req.getEnterPointId());
    }
}
