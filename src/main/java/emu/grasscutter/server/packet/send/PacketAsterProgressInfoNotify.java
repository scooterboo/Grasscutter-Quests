package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.aster.AsterLargeDetailInfo;
import messages.activity.aster.AsterLargeInfoNotify;
import messages.activity.aster.AsterProgressDetailInfo;
import messages.activity.aster.AsterProgressInfoNotify;

public class PacketAsterProgressInfoNotify extends BaseTypedPacket<AsterProgressInfoNotify> {

	public PacketAsterProgressInfoNotify(AsterProgressDetailInfo activityInfo) {
		super(new AsterProgressInfoNotify(activityInfo));
	}
}
