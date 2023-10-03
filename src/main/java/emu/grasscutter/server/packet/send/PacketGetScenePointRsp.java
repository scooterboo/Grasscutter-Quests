package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.GetScenePointRspOuterClass.GetScenePointRsp;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

public class PacketGetScenePointRsp extends BasePacket {
    private static final Set<Integer> bruteForceScenePointIdList = buildBruteForceScenePointIdList();

    public PacketGetScenePointRsp(Player player, int sceneId) {
        super(PacketOpcodes.GetScenePointRsp);

        val unlockedAreaList = player.getUnlockedSceneAreas(sceneId);
        val scenePointIdList = GameData.getScenePointIdList().isEmpty() ? bruteForceScenePointIdList :
            player.getUnlockedScenePoints(sceneId);

        val proto = GetScenePointRsp.newBuilder()
            .setSceneId(sceneId)
            .addAllUnhidePointList(scenePointIdList)
            .addAllUnlockedPointList(scenePointIdList)
            .addAllUnlockAreaList(unlockedAreaList);

        this.setData(proto);
    }

    private static Set<Integer> buildBruteForceScenePointIdList() {
        val set = new HashSet<Integer>(1000);
        for (int i = 1; i < 1000; i++) {
            set.add(i);
        }

        return set;
    }
}
