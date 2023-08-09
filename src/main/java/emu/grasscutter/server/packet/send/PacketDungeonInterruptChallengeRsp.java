package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DungeonInterruptChallengeReqOuterClass.DungeonInterruptChallengeReq;
import emu.grasscutter.net.proto.DungeonInterruptChallengeRspOuterClass.DungeonInterruptChallengeRsp;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;

public class PacketDungeonInterruptChallengeRsp extends BasePacket {
    public PacketDungeonInterruptChallengeRsp(boolean result, int challengeId, int challengeIndex, int groupId) {
        super(PacketOpcodes.DungeonInterruptChallengeRsp);
        this.setData(DungeonInterruptChallengeRsp.newBuilder()
            .setChallengeId(challengeId)
            .setChallengeIndex(challengeIndex)
            .setGroupId(groupId)
            .setRetcode(result ? Retcode.RET_SUCC_VALUE : Retcode.RET_FAIL_VALUE));
    }
}
