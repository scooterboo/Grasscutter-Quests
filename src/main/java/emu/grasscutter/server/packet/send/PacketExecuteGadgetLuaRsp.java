
package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.first.ExecuteGadgetLuaRsp;

public class PacketExecuteGadgetLuaRsp extends BaseTypedPacket<ExecuteGadgetLuaRsp> {
    public PacketExecuteGadgetLuaRsp(int result) {
        super(new ExecuteGadgetLuaRsp(), true);
        proto.setRetcode(result);
    }
}
