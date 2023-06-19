package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import messages.shop.GetShopmallDataRsp;

import java.util.List;

public class PacketGetShopmallDataRsp extends BaseTypedPackage<GetShopmallDataRsp> {

	public PacketGetShopmallDataRsp() {
		super(new GetShopmallDataRsp());

		val shop_malls = List.of(900, 1052, 902, 1001, 903);

		proto.setShopTypeList(shop_malls);
	}
}
