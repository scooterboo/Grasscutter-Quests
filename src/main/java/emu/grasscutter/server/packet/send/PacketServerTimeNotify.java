package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.general.ServerTimeNotify;

public class PacketServerTimeNotify extends BaseTypedPacket<ServerTimeNotify> {

	public PacketServerTimeNotify() {
		super(new ServerTimeNotify(System.currentTimeMillis()));
	}
}
