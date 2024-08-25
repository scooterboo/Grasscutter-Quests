package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.FireworkSetNotifyOuterClass;
import emu.grasscutter.net.proto.FireworkSetDataOuterClass;

public class PacketFireworksLaunchDataNotify extends BasePacket {

    public PacketFireworksLaunchDataNotify(FireworkSetDataOuterClass.FireworkSetData notify) {
        super(PacketOpcodes.Unk2700_NBFOJLAHFCA_ServerNotify);

        var proto
                = FireworkSetNotifyOuterClass.FireworkSetNotify.newBuilder();

        proto.setCode(1).addData(notify);

        setData(proto.build());
    }

}
