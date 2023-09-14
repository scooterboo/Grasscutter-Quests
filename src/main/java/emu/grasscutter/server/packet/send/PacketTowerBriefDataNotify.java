package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.TowerFloorData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.tower.TowerFloorRecordInfo;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.TowerBriefDataNotifyOuterClass.TowerBriefDataNotify;
import lombok.val;

import java.util.Comparator;

public class PacketTowerBriefDataNotify extends BasePacket {
    public PacketTowerBriefDataNotify(Player player) {
        super(PacketOpcodes.TowerBriefDataNotify);
        val towerManager = player.getTowerManager();
        val towerScheduleManager = player.getServer().getTowerSystem();
        val floorIndices = towerScheduleManager.getScheduleFloors().stream()
            .map(floorId -> GameData.getTowerFloorDataMap().get(floorId.intValue()))
            .map(TowerFloorData::getFloorIndex).filter(towerManager.getRecordMap()::containsKey).toList();
        val bestFloor = floorIndices.stream().max(Comparator.naturalOrder()).orElse(0);

        val bestFloorRecord = towerManager.getRecordMap().get(bestFloor);
        val lastLevelIndex = bestFloorRecord != null ? bestFloorRecord.getBestLevelIndex(): 0;

        this.setData(TowerBriefDataNotify.newBuilder()
            .setTotalStarNum(floorIndices.stream().map(towerManager.getRecordMap()::get)
                .mapToInt(TowerFloorRecordInfo::getStarCount).sum())
            .setIsFinishedEntranceFloor(towerManager.canEnterScheduleFloor())
            .setScheduleStartTime(towerScheduleManager.getScheduleStartDate())
            .setLastFloorIndex(bestFloor)
            .setLastLevelIndex(lastLevelIndex)
            .setNextScheduleChangeTime(towerScheduleManager.getScheduleChangeDate())
            .setTowerScheduleId(towerScheduleManager.getCurrentTowerScheduleData().getScheduleId()));
    }
}
