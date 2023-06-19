package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.server.game.GameSession;
import messages.gadget.LiveStartNotify;

public class PacketLiveStartNotify extends BaseTypedPackage<LiveStartNotify> {

	public PacketLiveStartNotify(int live_id) {
		super(new LiveStartNotify(live_id));
	}
}
