package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.tower.TowerData;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.tower.TowerEnterLevelRsp;

public class PacketTowerEnterLevelRsp extends BaseTypedPacket<TowerEnterLevelRsp> {
    public PacketTowerEnterLevelRsp(TowerData data) {
        super(new TowerEnterLevelRsp());
        proto.setFloorId(data.getCurrentFloorId());
        proto.setLevelIndex(data.getCurrentLevelIndex());
        proto.setTowerBuffIdList(data.getRandomBuffs());
    }
}
