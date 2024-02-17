package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.aster.AsterLittleDetailInfo;
import messages.activity.aster.AsterLittleInfoNotify;
import messages.activity.aster.AsterMidCampInfo;
import messages.activity.aster.AsterMidCampInfoNotify;

public class PacketAsterMidCampInfoNotify extends BaseTypedPacket<AsterMidCampInfoNotify> {

	public PacketAsterMidCampInfoNotify(AsterMidCampInfo activityInfo) {
		super(new AsterMidCampInfoNotify(activityInfo));
	}
}
