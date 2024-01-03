package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.gadget.LiveStartNotify;

public class PacketLiveStartNotify extends BaseTypedPacket<LiveStartNotify> {

	public PacketLiveStartNotify(int live_id) {
		super(new LiveStartNotify(live_id));
	}
}
