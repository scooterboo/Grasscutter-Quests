package emu.grasscutter.server.packet.send;

import java.util.Collection;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.gadget.GadgetAutoPickDropInfoNotify;

public class PacketGadgetAutoPickDropInfoNotify extends BaseTypedPacket<GadgetAutoPickDropInfoNotify> {

	public PacketGadgetAutoPickDropInfoNotify(Collection<GameItem> items) {
		super(new GadgetAutoPickDropInfoNotify());

        proto.setItemList(items.stream().map(GameItem::toProto).toList());
	}
}
