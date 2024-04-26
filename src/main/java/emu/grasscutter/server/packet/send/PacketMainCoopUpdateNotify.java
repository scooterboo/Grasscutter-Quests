package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import messages.coop.*;
import java.util.ArrayList;
import java.util.List;

public class PacketMainCoopUpdateNotify extends BaseTypedPacket<MainCoopUpdateNotify> {

    public PacketMainCoopUpdateNotify(List<MainCoop> mainCoops) {
        super(new MainCoopUpdateNotify());
        val mainCoopList = new ArrayList<MainCoop>();
        for (val mainCoop : mainCoops) {
            if (mainCoop.getId() != 0) {
                mainCoopList.add(mainCoop);
            }
        }
        proto.setMainCoopList(mainCoopList);
    }
}