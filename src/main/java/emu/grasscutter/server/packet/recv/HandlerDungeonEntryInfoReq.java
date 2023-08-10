package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DungeonEntryInfoReqOuterClass.DungeonEntryInfoReq;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonEntryInfoRsp;
import lombok.val;

@Opcodes(PacketOpcodes.DungeonEntryInfoReq)
public class HandlerDungeonEntryInfoReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        val req = DungeonEntryInfoReq.parseFrom(payload);

        session.getPlayer().sendPacket(new PacketDungeonEntryInfoRsp(session.getPlayer(), req.getSceneId(), req.getPointId()));
    }
}
