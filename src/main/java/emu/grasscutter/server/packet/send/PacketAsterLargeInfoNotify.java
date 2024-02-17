package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.aster.AsterLargeDetailInfo;
import messages.activity.aster.AsterLargeInfoNotify;
import messages.activity.aster.AsterMidDetailInfo;
import messages.activity.aster.AsterMidInfoNotify;

public class PacketAsterLargeInfoNotify extends BaseTypedPacket<AsterLargeInfoNotify> {

	public PacketAsterLargeInfoNotify(AsterLargeDetailInfo activityInfo) {
		super(new AsterLargeInfoNotify(activityInfo));
	}
}
