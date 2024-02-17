package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import messages.chat.ChatInfo;
import messages.chat.PlayerChatNotify;
import messages.chat.SystemHint;

public class PacketPlayerChatNotify extends BaseTypedPacket<PlayerChatNotify> {

	public PacketPlayerChatNotify(Player sender, int channelId, String message) {
		this(sender, channelId, new ChatInfo.Content.Text(message));
	}

	public PacketPlayerChatNotify(Player sender, int channelId, int emote) {
        this(sender, channelId, new ChatInfo.Content.Icon(emote));
	}

	public PacketPlayerChatNotify(Player sender, int channelId, SystemHint systemHint) {

        this(sender, channelId, new ChatInfo.Content.SystemHint(systemHint));
	}


    public PacketPlayerChatNotify(Player sender, int channelId, ChatInfo.Content<?> content) {
        super(new PlayerChatNotify());

        val info = new ChatInfo((int) (System.currentTimeMillis() / 1000), sender.getUid());
        info.setContent(content);

        proto.setChannelId(channelId);
        proto.setChatInfo(info);
    }
}
