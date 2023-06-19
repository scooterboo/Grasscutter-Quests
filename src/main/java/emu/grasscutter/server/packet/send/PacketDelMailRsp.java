package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.server.game.GameSession;
import messages.mail.DelMailRsp;

import java.util.List;

public class PacketDelMailRsp  extends BaseTypedPackage<DelMailRsp> {

    public PacketDelMailRsp(List<Integer> toDeleteIds) {
        super(new DelMailRsp(toDeleteIds));
    }
}
