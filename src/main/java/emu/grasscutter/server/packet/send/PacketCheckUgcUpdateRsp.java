package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.server.game.GameSession;
import messages.activity.user_generated_content.CheckUgcUpdateRsp;
import messages.activity.user_generated_content.UgcType;

public class PacketCheckUgcUpdateRsp extends BaseTypedPackage<CheckUgcUpdateRsp> {

	public PacketCheckUgcUpdateRsp(UgcType ugcType) {
		super(new CheckUgcUpdateRsp(ugcType));
	}
}
