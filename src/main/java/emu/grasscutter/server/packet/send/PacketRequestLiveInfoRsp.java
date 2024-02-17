package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.gadget.RequestLiveInfoRsp;

public class PacketRequestLiveInfoRsp extends BaseTypedPacket<RequestLiveInfoRsp> {

	public PacketRequestLiveInfoRsp(int live_id, String live_url) {
		super(new RequestLiveInfoRsp(live_id,live_url));

        proto.setRetCode(0);
	}
}
