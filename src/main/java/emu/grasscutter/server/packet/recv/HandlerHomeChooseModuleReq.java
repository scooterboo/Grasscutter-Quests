package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import org.anime_game_servers.multi_proto.gi.messages.home.HomeChooseModuleReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeChooseModuleRsp;
import emu.grasscutter.server.packet.send.PacketHomeComfortInfoNotify;
import emu.grasscutter.server.packet.send.PacketPlayerHomeCompInfoNotify;

public class HandlerHomeChooseModuleReq extends TypedPacketHandler<HomeChooseModuleReq> {
    @Override
    public void handle(GameSession session, byte[] header, HomeChooseModuleReq req) throws Exception {
        session.getPlayer().addRealmList(req.getModuleId());
        session.getPlayer().setCurrentRealmId(req.getModuleId());
        session.send(new PacketHomeChooseModuleRsp(req.getModuleId()));
        session.send(new PacketPlayerHomeCompInfoNotify(session.getPlayer()));
        session.send(new PacketHomeComfortInfoNotify(session.getPlayer()));
    }
}
