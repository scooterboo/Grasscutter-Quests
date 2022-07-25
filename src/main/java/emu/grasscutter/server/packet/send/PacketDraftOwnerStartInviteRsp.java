package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DraftOwnerStartInviteRspOuterClass;
import emu.grasscutter.net.proto.RetcodeOuterClass;

public class PacketDraftOwnerStartInviteRsp extends BasePacket {

	public PacketDraftOwnerStartInviteRsp(int draftID) {
		super(PacketOpcodes.DraftOwnerStartInviteRsp);

		this.setData(DraftOwnerStartInviteRspOuterClass.DraftOwnerStartInviteRsp.newBuilder()
                .setRetcode(RetcodeOuterClass.Retcode.RET_SUCC_VALUE)
                .setDraftId(draftID)
                .setWrongUid(10)
            .build());
	}
}
