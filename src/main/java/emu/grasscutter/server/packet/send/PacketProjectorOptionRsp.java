package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.server.game.GameSession;
import messages.gadget.ProjectorOptionRsp;

public class PacketProjectorOptionRsp extends BaseTypedPackage<ProjectorOptionRsp> {

    public PacketProjectorOptionRsp(int entityId, int opType, int retcode) {
        super(new ProjectorOptionRsp(opType, entityId, retcode));
    }
}
