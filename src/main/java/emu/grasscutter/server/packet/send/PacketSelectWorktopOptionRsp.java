package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.gadget.SelectWorktopOptionRsp;

public class PacketSelectWorktopOptionRsp extends BaseTypedPacket<SelectWorktopOptionRsp> {

	public PacketSelectWorktopOptionRsp(int entityId, int optionId) {
		super(new SelectWorktopOptionRsp(entityId, optionId));
	}
}
