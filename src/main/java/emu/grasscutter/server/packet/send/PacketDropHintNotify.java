package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.item.DropHintNotify;

import java.util.List;

public class PacketDropHintNotify extends BaseTypedPacket<DropHintNotify> {

    public PacketDropHintNotify(List<Integer> itemIdList, Vector position) {
        super(new DropHintNotify(itemIdList, position));
    }
}
