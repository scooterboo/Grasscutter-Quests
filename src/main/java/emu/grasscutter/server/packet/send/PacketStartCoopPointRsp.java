package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.StartCoopPointRsp;

public class PacketStartCoopPointRsp extends BaseTypedPacket<StartCoopPointRsp> {

	public PacketStartCoopPointRsp(int coopPoint) {
		super(new StartCoopPointRsp(coopPoint));
	}
}