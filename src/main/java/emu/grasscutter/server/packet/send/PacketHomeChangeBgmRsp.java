package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.Unk2700OGHMHELMBNNServerRsp;

public class PacketHomeChangeBgmRsp extends BasePacket {
    public PacketHomeChangeBgmRsp() {
        super(PacketOpcodes.HomeChangeBgmRsp);

        var rsp = Unk2700OGHMHELMBNNServerRsp.Unk2700_OGHMHELMBNN_ServerRsp.newBuilder()
            .setRetcode(0)
            .build();

        this.setData(rsp);
    }
}
