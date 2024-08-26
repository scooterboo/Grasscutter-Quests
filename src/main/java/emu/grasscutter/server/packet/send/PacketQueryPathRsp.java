package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.QueryPathReq;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.QueryPathRsp;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.PathStatusType;

import java.util.List;

public class PacketQueryPathRsp extends BaseTypedPacket<QueryPathRsp> {
    public PacketQueryPathRsp(QueryPathReq req) {
        super(new QueryPathRsp());
        proto.setCorners(List.of(req.getSourcePos(), req.getDestinationPos().get(0)));
        proto.setQueryId(req.getQueryId());
        proto.setQueryStatus(PathStatusType.STATUS_SUCC);
	}
}
