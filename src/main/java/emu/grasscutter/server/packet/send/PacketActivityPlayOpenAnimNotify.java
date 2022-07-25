package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.ActivityPlayOpenAnimNotifyOuterClass.ActivityPlayOpenAnimNotify;

public class PacketActivityPlayOpenAnimNotify extends BasePacket {

	public PacketActivityPlayOpenAnimNotify(int activityId) {
		super(PacketOpcodes.ActivityPlayOpenAnimNotify);

        var proto = ActivityPlayOpenAnimNotify.newBuilder();

        proto.setActivityId(activityId);

        this.setData(proto);
	}
}
