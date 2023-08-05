package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.RequestLiveInfoRspOuterClass.RequestLiveInfoRsp;

public class PacketRequestLiveInfoRsp extends BasePacket {

	public PacketRequestLiveInfoRsp(int live_id, String live_url) {
		super(PacketOpcodes.RequestLiveInfoRsp);

		RequestLiveInfoRsp proto = RequestLiveInfoRsp.newBuilder()
				.setRetcode(0)
                .setLiveId(live_id)
                .setLiveUrl(live_url)
				.build();

		this.setData(proto);
	}
}
