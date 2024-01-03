package emu.grasscutter.server.packet.send;

import java.util.ArrayList;
import java.util.List;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import messages.chat.ChatInfo;
import messages.chat.PullPrivateChatRsp;

public class PacketPullPrivateChatRsp extends BaseTypedPacket<PullPrivateChatRsp> {

    public PacketPullPrivateChatRsp(List<ChatInfo> history) {
        super(new PullPrivateChatRsp());

        if (history == null) {
            proto.setRetCode(Retcode.RET_FAIL_VALUE);
        }
        else {
            proto.setChatInfo(new ArrayList<>(history));
        }
    }
}
