package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.home.FurnitureMakeData;
import org.anime_game_servers.multi_proto.gi.messages.home.FurnitureMakeSlot;
import org.anime_game_servers.multi_proto.gi.messages.home.FurnitureMakeStartRsp;

import java.util.List;

public class PacketFurnitureMakeStartRsp extends BaseTypedPacket<FurnitureMakeStartRsp> {
    public PacketFurnitureMakeStartRsp(int ret, List<FurnitureMakeData> furnitureMakeData) {
        super(new FurnitureMakeStartRsp());
        proto.setRetcode(ret);

        if (furnitureMakeData != null) {
            val furnitureMakeSlot = new FurnitureMakeSlot();
            furnitureMakeSlot.setFurnitureMakeDataList(furnitureMakeData);
            proto.setFurnitureMakeSlot(furnitureMakeSlot);
        }
    }
}
