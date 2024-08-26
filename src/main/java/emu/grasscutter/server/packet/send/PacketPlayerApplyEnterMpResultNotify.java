package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.PlayerApplyEnterMpResultNotify;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.Reason;

public class PacketPlayerApplyEnterMpResultNotify extends BaseTypedPacket<PlayerApplyEnterMpResultNotify> {
    public PacketPlayerApplyEnterMpResultNotify(Player target, boolean isAgreed, Reason reason) {
        super(new PlayerApplyEnterMpResultNotify());
        proto.setTargetUid(target.getUid());
        proto.setTargetNickname(target.getNickname());
        proto.setAgreed(isAgreed);
        proto.setReason(reason);
	}

    public PacketPlayerApplyEnterMpResultNotify(int targetId, String targetName, boolean isAgreed, Reason reason) {
        super(new PlayerApplyEnterMpResultNotify());
        proto.setTargetUid(targetId);
        proto.setTargetNickname(targetName);
        proto.setAgreed(isAgreed);
        proto.setReason(reason);
	}
}
