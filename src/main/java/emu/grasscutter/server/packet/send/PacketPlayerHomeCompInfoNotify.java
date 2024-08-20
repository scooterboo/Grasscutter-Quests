package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.home.PlayerHomeCompInfo;
import org.anime_game_servers.multi_proto.gi.messages.home.PlayerHomeCompInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.FriendEnterHomeOption;

import java.util.List;

public class PacketPlayerHomeCompInfoNotify extends BaseTypedPacket<PlayerHomeCompInfoNotify> {
    public PacketPlayerHomeCompInfoNotify(Player player) {
        super(new PlayerHomeCompInfoNotify());
        if (player.getRealmList() == null) {
            // Do not send
            return;
        }
        val playerHomeCompInfo = new PlayerHomeCompInfo();
        playerHomeCompInfo.setUnlockedModuleIdList(player.getRealmList().stream().toList());
        playerHomeCompInfo.setLevelupRewardGotLevelList(List.of(1)); // Hardcoded
        playerHomeCompInfo.setFriendEnterHomeOption(FriendEnterHomeOption.values()[player.getHome().getEnterHomeOption()]);
        proto.setCompInfo(playerHomeCompInfo);
    }
}
