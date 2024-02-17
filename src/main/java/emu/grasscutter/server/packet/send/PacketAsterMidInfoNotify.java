package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.ActivityInfo;
import messages.activity.ActivityInfoNotify;
import messages.activity.aster.AsterMidDetailInfo;
import messages.activity.aster.AsterMidInfoNotify;

public class PacketAsterMidInfoNotify extends BaseTypedPacket<AsterMidInfoNotify> {

	public PacketAsterMidInfoNotify(AsterMidDetailInfo activityInfo) {
		super(new AsterMidInfoNotify(activityInfo));
	}
}
