package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.FinishMainCoopRsp;

public class PacketFinishMainCoopRsp extends BaseTypedPacket<FinishMainCoopRsp> {

    public PacketFinishMainCoopRsp(int Id, int endingSavePointId) {
        super(new FinishMainCoopRsp(endingSavePointId, Id));
    }
}