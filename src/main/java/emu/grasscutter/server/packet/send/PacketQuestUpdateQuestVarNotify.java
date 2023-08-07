package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.QuestUpdateQuestVarNotifyOuterClass.QuestUpdateQuestVarNotify;
import lombok.val;

import java.util.stream.IntStream;

@Opcodes(PacketOpcodes.QuestUpdateQuestVarNotify)
public class PacketQuestUpdateQuestVarNotify extends BasePacket {

    public PacketQuestUpdateQuestVarNotify(int mainQuestId, int... questVars) {
        super(PacketOpcodes.QuestUpdateQuestVarNotify);
        val questVarList = IntStream.of(questVars).boxed().toList();
        this.setData(QuestUpdateQuestVarNotify.newBuilder()
            .setParentQuestId(mainQuestId)
            .addAllQuestVar(questVarList));
    }
}
