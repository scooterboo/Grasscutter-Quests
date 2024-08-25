package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerTeamSelectRsp;

public class PacketTowerTeamSelectRsp extends BaseTypedPacket<TowerTeamSelectRsp> {
    public PacketTowerTeamSelectRsp(boolean result) {
        super(new TowerTeamSelectRsp());
        proto.setRetcode(result ? 0 : 1);
    }
}
