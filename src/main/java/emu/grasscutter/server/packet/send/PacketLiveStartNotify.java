package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.LiveStartNotifyOuterClass.LiveStartNotify;

public class PacketLiveStartNotify extends BasePacket {

	public PacketLiveStartNotify(int live_id) {
		super(PacketOpcodes.LiveStartNotify);

		LiveStartNotify proto = LiveStartNotify.newBuilder()
                .setLiveId(live_id)
				.build();

		this.setData(proto);
	}
}
