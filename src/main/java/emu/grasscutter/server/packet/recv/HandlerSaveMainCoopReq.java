package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMainCoopUpdateNotify;
import emu.grasscutter.server.packet.send.PacketSaveMainCoopRsp;
import lombok.val;
import messages.coop.SaveMainCoopReq;
import java.util.HashSet;
import java.util.List;

public class HandlerSaveMainCoopReq extends TypedPacketHandler<SaveMainCoopReq> {

    @Override
    public void handle(GameSession session, byte[] header, SaveMainCoopReq req) throws Exception {

        //save
        val coopCardMainCoop = session.getPlayer().getCoopCards().get(req.getId()).getMainCoop().toProto();
        coopCardMainCoop.setId(req.getId());
        coopCardMainCoop.setNormalVarMap(req.getNormalVarMap());
        val tmpSet = new HashSet<>(coopCardMainCoop.getSavePointIdList());
        tmpSet.add(req.getSavePointId());
        coopCardMainCoop.setSavePointIdList(tmpSet.stream().toList());
        coopCardMainCoop.setSelfConfidence(req.getSelfConfidence());
        coopCardMainCoop.setTempVarMap(req.getTempVarMap());
        session.getPlayer().getCoopCards().get(req.getId()).getMainCoop().fromProto(coopCardMainCoop);

        //send mainCoop packet
        session.send(new PacketMainCoopUpdateNotify(List.of(coopCardMainCoop)));

        //update quests
        val questManager = session.getPlayer().getQuestManager();
        Grasscutter.getLogger().info("savecoop: {} {}", req.getId(), req.getSavePointId());
        questManager.queueEvent(QuestContent.QUEST_CONTENT_MAIN_COOP_ENTER_SAVE_POINT, req.getId(), req.getSavePointId());
        questManager.queueEvent(QuestContent.QUEST_CONTENT_MAIN_COOP_ENTER_ANY_SAVE_POINT, req.getId());
        questManager.queueEvent(QuestCond.QUEST_COND_MAIN_COOP_ENTER_SAVE_POINT, req.getId(), req.getSavePointId());

        //TODO finish coop point
        //session.send(new PacketCoopPointUpdateNotify()));

        //TODO start next coop point
        //session.send(new PacketCoopPointUpdateNotify()));

        //TODO update progress
        //session.send(new PacketCoopProgressUpdateNotify()));

        //respond
        session.send(new PacketSaveMainCoopRsp(req.getId(), List.of(req.getSavePointId())));
    }

}