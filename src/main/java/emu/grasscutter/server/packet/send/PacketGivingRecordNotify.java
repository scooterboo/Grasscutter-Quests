package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.GivingRecordNotify;

public class PacketGivingRecordNotify extends BaseTypedPacket<GivingRecordNotify> {
    public PacketGivingRecordNotify(Player player) {
        super(new GivingRecordNotify());
        proto.setGivingRecordList(player.getGivingManager().getGivingRecords());
    }
}
