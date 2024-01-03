package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.chat.PlayerChatRsp;

public class PacketPlayerChatRsp extends BaseTypedPacket<PlayerChatRsp> {

	public PacketPlayerChatRsp() {
		super(new PlayerChatRsp());
	}
}
