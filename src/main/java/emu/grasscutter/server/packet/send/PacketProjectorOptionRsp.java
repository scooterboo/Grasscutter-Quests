package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.ProjectorOptionRspOuterClass.ProjectorOptionRsp;

public class PacketProjectorOptionRsp extends BasePacket {

    public PacketProjectorOptionRsp(int entityId, int opType, int retcode) {
        super(PacketOpcodes.ProjectorOptionRsp);

        ProjectorOptionRsp proto = ProjectorOptionRsp.newBuilder()
                .setEntityId(entityId)
                .setOpType(opType)
                .setRetcode(retcode)
                .build();

        this.setData(proto);
    }
}
