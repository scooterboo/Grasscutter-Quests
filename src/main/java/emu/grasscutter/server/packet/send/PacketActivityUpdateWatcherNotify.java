package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.activity.ActivityUpdateWatcherNotify;

public class PacketActivityUpdateWatcherNotify extends BaseTypedPackage<ActivityUpdateWatcherNotify> {

	public PacketActivityUpdateWatcherNotify(int activityId, PlayerActivityData.WatcherInfo watcherInfo) {
		super(new ActivityUpdateWatcherNotify(
            activityId, watcherInfo.toProto()
        ));
	}
}
