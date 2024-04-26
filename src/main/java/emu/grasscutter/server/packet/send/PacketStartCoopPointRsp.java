package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import messages.coop.MainCoop;
import messages.coop.StartCoopPointRsp;
import messages.coop.Status;
import java.util.HashMap;

public class PacketStartCoopPointRsp extends BaseTypedPacket<StartCoopPointRsp> {

	public PacketStartCoopPointRsp(int coopPoint, MainCoop startCoop) {
		super(new StartCoopPointRsp(coopPoint));
		proto.setStart(true);
		proto.setStartMainCoop(startCoop);
	}
}