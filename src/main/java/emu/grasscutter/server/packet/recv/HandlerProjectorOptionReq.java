package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.ProjectorOptionReqOuterClass.ProjectorOptionReq;
import emu.grasscutter.net.proto.ProjectorOptionReqOuterClass.ProjectorOptionReq.ProjectorOpType;
import emu.grasscutter.scripts.constants.ScriptGadgetState;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketProjectorOptionRsp;

@Opcodes(PacketOpcodes.ProjectorOptionReq)
public class HandlerProjectorOptionReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        ProjectorOptionReq req = ProjectorOptionReq.parseFrom(payload);

        Grasscutter.getLogger().debug("JUST SOME DEBUG " + req.getOpType());

        if(session.getPlayer().getScene().getEntityById(req.getEntityId()) instanceof EntityGadget gadget) {
            ProjectorOpType type = ProjectorOpType.forNumber(req.getOpType());
            if(type == ProjectorOpType.PROJECTOR_OP_TYPE_CREATE) {
                if(gadget.getState() != ScriptGadgetState.GearStart)
                    gadget.setState(ScriptGadgetState.GearStart);
            } else if(type == ProjectorOpType.PROJECTOR_OP_TYPE_DESTROY) {
                if(gadget.getState() != ScriptGadgetState.GearStop)
                    gadget.setState(ScriptGadgetState.GearStop);
            }

            session.send(new PacketProjectorOptionRsp(req.getEntityId(), req.getOpType(), 0));
        } else {
            session.send(new PacketProjectorOptionRsp(req.getEntityId(), req.getOpType(), 1));
        }
    }

}
