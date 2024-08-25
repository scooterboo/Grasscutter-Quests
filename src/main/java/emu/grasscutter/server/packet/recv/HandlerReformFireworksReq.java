package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.FireWorkReqOuterClass;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketFireworksReformDataNotify;
import emu.grasscutter.server.packet.send.PacketReformFireworksRsp;

@Opcodes(PacketOpcodes.ReformFireworksReq)
public class HandlerReformFireworksReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {

        var req
                = FireWorkReqOuterClass.FireWorkReq.parseFrom(payload);
        session.send(new PacketFireworksReformDataNotify(req.getFireWorkData()));
        session.send(new PacketReformFireworksRsp());
    }
}
