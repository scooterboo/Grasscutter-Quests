package emu.grasscutter.server.packet.send;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.Player.CoopCardEntry;
import lombok.val;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.*;
import org.anime_game_servers.core.gi.enums.QuestState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacketCoopDataNotify extends BaseTypedPacket<CoopDataNotify> {

    public PacketCoopDataNotify(Player player) {
        super(new CoopDataNotify());

        val chapterList = new ArrayList<CoopChapter>();

        GameData.getCoopChapterDataMap().values().forEach(c -> {
            val chapter = new CoopChapter();
            chapter.setId(c.getId());
            val coopCardEntry = player.getCoopCards().computeIfAbsent(c.getId(), v -> new CoopCardEntry(c.getId()));

            //State
            if (coopCardEntry.getAccepted()) {
                chapter.setState(CoopChapterState.STATE_ACCEPT);
            } else {
                List<Integer> lockReasonList = new ArrayList<>();
                for (int i = 0; i < c.getUnlockCond().size(); ++i) {
                    val condition = c.getUnlockCond().get(i);
                    val arg = condition.getArgs()[0];
                    switch (condition.getType()) {
                        case "COOP_COND_FINISH_QUEST" -> {
                            val quest = player.getQuestManager().getQuestById(arg);
                            if (!(quest != null && quest.getState().equals(QuestState.QUEST_STATE_FINISHED)))
                                lockReasonList.add(i + 1);
                        }
                        case "COOP_COND_PLAYER_LEVEL" -> {
                            if (!(player.getLevel() >= arg))
                                lockReasonList.add(i + 1);
                        }
                        case "COOP_COND_CHAPTER_END_ALL_FINISH" -> {
                            val card = player.getCoopCards().get(arg);
                            if (!(card.getFinishedEndCount() == card.getTotalEndCount()))
                                lockReasonList.add(i + 1);
                        }
                        default -> {
                            Grasscutter.getLogger().warn("Unknown Coop condition type {} in coop chapter {}", condition.getType(), c.getId());
                            lockReasonList.add(i + 1);
                        }
                    }

                }
                if (lockReasonList.isEmpty()) {
                    chapter.setState(CoopChapterState.STATE_COND_MEET);
                } else {
                    chapter.setState(CoopChapterState.STATE_COND_NOT_MEET);
                    chapter.setLockReasonList(lockReasonList);
                }
            }

            //TotalEndCount
            chapter.setTotalEndCount(coopCardEntry.getTotalEndCount());

            //Points
            List<CoopPoint> pointList = new ArrayList<>();
            coopCardEntry.getPoints().entrySet().stream()
                    .filter(e -> e.getValue().getState() != CoopPointState.STATE_UNSTARTED)
                    .forEach(e -> {
                        val point = new CoopPoint();
                        point.setId(e.getKey());
                        point.setState(e.getValue().getState());
                        pointList.add(point);
                    });
            chapter.setCoopPointList(pointList);

            chapterList.add(chapter);
        });

        //viewed list
        proto.setViewedChapterList(player.getCoopCards()
                .entrySet().stream()
                .filter(x -> x.getValue().getViewed())
                .map(Map.Entry::getKey)
                .toList());

        proto.setChapterList(chapterList);
    }
}