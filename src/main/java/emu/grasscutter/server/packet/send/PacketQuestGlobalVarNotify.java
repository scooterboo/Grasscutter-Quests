package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.quest.QuestGlobalVarNotify;
import messages.quest.QuestGlobalVar;

public class PacketQuestGlobalVarNotify extends BaseTypedPacket<QuestGlobalVarNotify> {
    public PacketQuestGlobalVarNotify(Player player) {
        super(new QuestGlobalVarNotify(player.getQuestGlobalVariables().entrySet().stream()
            .map(entry -> new QuestGlobalVar(entry.getKey(),entry.getValue())).toList()));
    }
}
