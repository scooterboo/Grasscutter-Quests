package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.tower.TowerBuffSelectRsp;

public class PacketTowerBuffSelectRsp extends BaseTypedPacket<TowerBuffSelectRsp> {
    public PacketTowerBuffSelectRsp(int towerBuffId) {
        super(new TowerBuffSelectRsp());
        proto.setTowerBuffId(towerBuffId);
    }
}
