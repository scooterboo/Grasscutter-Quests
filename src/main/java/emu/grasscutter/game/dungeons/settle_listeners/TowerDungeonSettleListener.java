package emu.grasscutter.game.dungeons.settle_listeners;

import emu.grasscutter.game.dungeons.DungeonEndStats;
import emu.grasscutter.game.dungeons.DungeonManager;
import emu.grasscutter.game.dungeons.dungeon_results.BaseDungeonResult.DungeonEndReason;
import emu.grasscutter.game.dungeons.dungeon_results.TowerResult;
import emu.grasscutter.server.packet.send.PacketDungeonSettleNotify;
import lombok.val;


public class TowerDungeonSettleListener implements DungeonSettleListener {

    @Override
    public void onDungeonSettle(DungeonManager dungeonManager, DungeonEndReason endReason) {
        val scene = dungeonManager.getScene();
        val hostPlayer = scene.getWorld().getHost();
        val towerManager = hostPlayer.getTowerManager();
        val time = scene.getSceneTimeSeconds() - dungeonManager.getStartSceneTime();
        val result = TowerResult.TowerBuilder()
            .setDungeonData(dungeonManager.getDungeonData())
            .setDungeonStats(new DungeonEndStats(scene.getKilledMonsterCount(), time, 0, endReason))
            .setPlayer(hostPlayer)
            .setHasNextFloor(towerManager.hasNextFloor())
            .setHasNextLevel(towerManager.hasNextLevel())
            .setNextFloorId(towerManager.getNextFloorId())
            .setStars(towerManager.calculateStar(dungeonManager, scene.getChallenge()))
            // give rewards should always be after calculating star, as it needs the result to be out before giving rewards
            .setRewardItems(towerManager.giveFirstPassReward(dungeonManager))
            .build();
        towerManager.updateNextLevel(dungeonManager); // only update after the current level has finished calculating

        hostPlayer.sendPacket(new PacketDungeonSettleNotify(result));
    }
}
