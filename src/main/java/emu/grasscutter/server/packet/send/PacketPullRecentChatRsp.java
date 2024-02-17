package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.chat.ChatInfo;
import messages.chat.PullRecentChatRsp;

import java.util.List;

public class PacketPullRecentChatRsp extends BaseTypedPacket<PullRecentChatRsp> {
    public PacketPullRecentChatRsp(List<ChatInfo> messages) {
        super(new PullRecentChatRsp(messages));
    }
}
