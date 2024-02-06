package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.aster.AsterLittleDetailInfo;
import messages.activity.aster.AsterLittleInfoNotify;
import messages.activity.aster.AsterMiscInfoNotify;

public class PacketAsterMiscInfoNotify extends BaseTypedPacket<AsterMiscInfoNotify> {

	public PacketAsterMiscInfoNotify(int credit, int token) {
		super(new AsterMiscInfoNotify(credit, token));
	}
}
