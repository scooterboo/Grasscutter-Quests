package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DraftOwnerStartInviteReqOuterClass.DraftOwnerStartInviteReq;
import emu.grasscutter.net.proto.DraftOwnerStartInviteRspOuterClass.DraftOwnerStartInviteRsp;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.server.packet.send.PacketDraftOwnerStartInviteRsp;

import static emu.grasscutter.utils.Utils.bytesToHex;

@Opcodes(PacketOpcodes.DraftOwnerStartInviteReq)
public class HandlerDraftOwnerStartInviteReq extends PacketHandler {

	@Override
	public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
		DraftOwnerStartInviteReq req = DraftOwnerStartInviteReq.parseFrom(payload);
		Grasscutter.getLogger().warn("Draft ID request: {} Hex: {}", req.getDraftId(), bytesToHex(payload));

        session.getPlayer().sendPacket(new PacketDraftOwnerStartInviteRsp(req.getDraftId()));
		//session.getPlayer().getTeamManager().setupMpTeam(req.getAvatarGuidListList());
	}

}
