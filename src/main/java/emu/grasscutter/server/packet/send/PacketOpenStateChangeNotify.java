package emu.grasscutter.server.packet.send;

import java.util.Map;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.player.OpenStateChangeNotify;

//Sets openState to value
public class PacketOpenStateChangeNotify extends BaseTypedPacket<OpenStateChangeNotify> {

    public PacketOpenStateChangeNotify(int openState, int value) {
        super(new OpenStateChangeNotify(Map.of(openState, value)));
    }

    public PacketOpenStateChangeNotify(Map<Integer, Integer> map) {
        super(new OpenStateChangeNotify(map));
    }

}
