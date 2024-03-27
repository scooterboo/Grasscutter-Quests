package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.GetSceneAreaRspOuterClass.GetSceneAreaRsp;
import lombok.val;

public class PacketGetSceneAreaRsp extends BasePacket {

    public PacketGetSceneAreaRsp(Player player, int sceneId) {
        super(PacketOpcodes.GetSceneAreaRsp);

        this.buildHeader(0);

        val p = GetSceneAreaRsp.newBuilder()
                .setSceneId(sceneId)
                .addAllAreaIdList(player.getUnlockedSceneAreas(sceneId));

        GameData.getCityDataMap().values().stream().filter(cityData -> cityData.getSceneId() == sceneId).forEach(cityData -> {
            p.addCityInfoList(player.getSotsManager().getCityInfo(cityData.getCityId()).toProto());
        });

        this.setData(p);
    }
}
