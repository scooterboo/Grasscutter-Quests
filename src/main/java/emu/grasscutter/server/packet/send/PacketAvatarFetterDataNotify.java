package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.props.FetterState;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.AvatarFetterDataNotifyOuterClass.AvatarFetterDataNotify;
import emu.grasscutter.net.proto.AvatarFetterInfoOuterClass.AvatarFetterInfo;
import emu.grasscutter.net.proto.FetterDataOuterClass.FetterData;
import lombok.val;

public class PacketAvatarFetterDataNotify extends BasePacket {

	public PacketAvatarFetterDataNotify(Avatar avatar) {
		super(PacketOpcodes.AvatarFetterDataNotify);

		int fetterLevel = avatar.getFetterLevel();

		val avatarFetter = AvatarFetterInfo.newBuilder()
				.setExpLevel(fetterLevel);
		if (fetterLevel != 10) avatarFetter.setExpNumber(avatar.getFetterExp());

        avatar.getFetters().stream().map(id -> FetterData.newBuilder().setFetterId(id)
                .setFetterState(FetterState.FINISH.getValue()))
            .forEach(avatarFetter::addFetterList);

		if (avatar.getPlayer().getNameCardList().contains(avatar.getNameCardId()))
            avatarFetter.addRewardedFetterLevelList(10);

		this.setData(AvatarFetterDataNotify.newBuilder()
            .putFetterInfoMap(avatar.getGuid(), avatarFetter.build())
            .build());
	}
}
