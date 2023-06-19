package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.activity.ActivityInfo;
import messages.activity.ActivityInfoNotify;

public class PacketActivityInfoNotify extends BaseTypedPackage<ActivityInfoNotify> {

	public PacketActivityInfoNotify(ActivityInfo activityInfo) {
		super(new ActivityInfoNotify(activityInfo));
	}
}
