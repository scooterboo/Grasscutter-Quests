package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.tower.TowerGetFloorStarRewardRsp;

public class PacketTowerGetFloorStarRewardRsp extends BaseTypedPacket<TowerGetFloorStarRewardRsp> {
    public PacketTowerGetFloorStarRewardRsp(boolean result, int floorId) {
        super(new TowerGetFloorStarRewardRsp());
        proto.setFloorId(floorId);
        proto.setRetcode(result ? 0 : 1);
    }
}
