package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.gadget.ProjectorOptionRsp;

public class PacketProjectorOptionRsp extends BaseTypedPacket<ProjectorOptionRsp> {

    public PacketProjectorOptionRsp(int entityId, int opType, int retcode) {
        super(new ProjectorOptionRsp(opType, entityId, retcode));
    }
}
