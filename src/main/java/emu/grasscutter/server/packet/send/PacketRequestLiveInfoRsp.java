package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.server.game.GameSession;
import messages.gadget.RequestLiveInfoRsp;

public class PacketRequestLiveInfoRsp extends BaseTypedPackage<RequestLiveInfoRsp> {

	public PacketRequestLiveInfoRsp(int live_id, String live_url) {
		super(new RequestLiveInfoRsp(live_id,live_url));

        proto.setRetCode(0);
	}
}
