package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.aster.AsterLittleDetailInfo;
import messages.activity.aster.AsterLittleInfoNotify;
import messages.activity.aster.AsterMidDetailInfo;
import messages.activity.aster.AsterMidInfoNotify;

public class PacketAsterLittleInfoNotify extends BaseTypedPacket<AsterLittleInfoNotify> {

	public PacketAsterLittleInfoNotify(AsterLittleDetailInfo activityInfo) {
		super(new AsterLittleInfoNotify(activityInfo));
	}
}
