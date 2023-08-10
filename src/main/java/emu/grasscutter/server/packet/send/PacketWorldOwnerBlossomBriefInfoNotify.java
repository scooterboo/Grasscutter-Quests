package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.WorldOwnerBlossomBriefInfoNotifyOuterClass.WorldOwnerBlossomBriefInfoNotify;

public class PacketWorldOwnerBlossomBriefInfoNotify extends BasePacket {
    public PacketWorldOwnerBlossomBriefInfoNotify(World world) {
        super(PacketOpcodes.WorldOwnerBlossomBriefInfoNotify);

        this.setData(WorldOwnerBlossomBriefInfoNotify.newBuilder()
            .addAllBriefInfoList(world.getOwner().getBlossomManager().getBriefInfo())
            .build());
    }
}
