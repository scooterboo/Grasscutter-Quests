package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.EquipType;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.item.AvatarEquipChangeNotify;

public class PacketAvatarEquipChangeNotify extends BaseTypedPacket<AvatarEquipChangeNotify> {

	public PacketAvatarEquipChangeNotify(Avatar avatar, GameItem item) {
		super(new AvatarEquipChangeNotify(avatar.getGuid(), item.getEquipSlot(), item.getItemId(), item.getGuid()));

		if (item.getItemData().getEquipType() == EquipType.EQUIP_WEAPON) {
			proto.setWeapon(item.createSceneWeaponInfo());
		} else {
			proto.setReliquary(item.createSceneReliquaryInfo());
		}
	}

	public PacketAvatarEquipChangeNotify(Avatar avatar, EquipType slot) {
		super(new AvatarEquipChangeNotify(avatar.getGuid(), slot.getValue()));
	}
}
