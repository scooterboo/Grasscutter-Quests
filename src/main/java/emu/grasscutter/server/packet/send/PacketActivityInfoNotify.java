package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.ActivityInfo;
import messages.activity.ActivityInfoNotify;

public class PacketActivityInfoNotify extends BaseTypedPacket<ActivityInfoNotify> {

	public PacketActivityInfoNotify(ActivityInfo activityInfo) {
		super(new ActivityInfoNotify(activityInfo));
	}
}
