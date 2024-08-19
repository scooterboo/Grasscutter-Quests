package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.ActivityUpdateWatcherNotify;

public class PacketActivityUpdateWatcherNotify extends BaseTypedPacket<ActivityUpdateWatcherNotify> {

	public PacketActivityUpdateWatcherNotify(int activityId, PlayerActivityData.WatcherInfo watcherInfo) {
        super(new ActivityUpdateWatcherNotify());
        proto.setActivityId(activityId);
        proto.setWatcherInfo(watcherInfo.toProto());
	}
}
