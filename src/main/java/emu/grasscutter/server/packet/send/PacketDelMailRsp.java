package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.mail.DelMailRsp;

import java.util.List;

public class PacketDelMailRsp  extends BaseTypedPacket<DelMailRsp> {

    public PacketDelMailRsp(List<Integer> toDeleteIds) {
        super(new DelMailRsp(toDeleteIds));
    }
}
