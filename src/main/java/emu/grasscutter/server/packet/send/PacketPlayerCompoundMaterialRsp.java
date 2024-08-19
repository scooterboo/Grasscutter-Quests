package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CompoundQueueData;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.PlayerCompoundMaterialRsp;

public class PacketPlayerCompoundMaterialRsp extends BaseTypedPacket<PlayerCompoundMaterialRsp> {
    /**
     * Builder for successful message.
     * @param compoundQueueData new compound queue
     */
    public PacketPlayerCompoundMaterialRsp(CompoundQueueData compoundQueueData) {
        super(new PlayerCompoundMaterialRsp());
        proto.setCompoundQueueData(compoundQueueData);
        proto.setRetcode(Retcode.RET_SUCC_VALUE);
    }

    /**
     * Builder for failed message.
     * @param retcode error code
     */
    public PacketPlayerCompoundMaterialRsp(int retcode) {
        super(new PlayerCompoundMaterialRsp());
        proto.setRetcode(retcode);
    }
}
