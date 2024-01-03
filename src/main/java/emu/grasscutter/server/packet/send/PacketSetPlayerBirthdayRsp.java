package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.SetPlayerBornDataReqOuterClass;
import messages.chat.SetPlayerBirthdayRsp;

public class PacketSetPlayerBirthdayRsp extends BaseTypedPacket<SetPlayerBirthdayRsp> {

	public PacketSetPlayerBirthdayRsp(int retCode) {
		super(new SetPlayerBirthdayRsp());

        proto.setRetCode(retCode);
	}

	public PacketSetPlayerBirthdayRsp(Player player) {
		super(new SetPlayerBirthdayRsp(player.getBirthday().toProto()));
	}
}
