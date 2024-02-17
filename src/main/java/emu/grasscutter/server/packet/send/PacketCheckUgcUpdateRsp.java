package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.user_generated_content.CheckUgcUpdateRsp;
import messages.activity.user_generated_content.UgcType;

public class PacketCheckUgcUpdateRsp extends BaseTypedPacket<CheckUgcUpdateRsp> {

	public PacketCheckUgcUpdateRsp(UgcType ugcType) {
		super(new CheckUgcUpdateRsp(ugcType));
	}
}
