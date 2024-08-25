package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.FireWorkNotifyOuterClass;
import emu.grasscutter.net.proto.FireWorkDataOuterClass;

public class PacketFireworksReformDataNotify extends BasePacket {

    public PacketFireworksReformDataNotify(FireWorkDataOuterClass.FireWorkData pinfo) {
        super(PacketOpcodes.Unk2700_MCJIOOELGHG_ServerNotify);

        var proto
                = FireWorkNotifyOuterClass.FireWorkNotify.newBuilder();

        proto.addFireWorkData(pinfo);

        setData(proto.build());
    }

}
