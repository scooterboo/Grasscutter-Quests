package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass;
import messages.activity.user_generated_content.GetUgcBriefInfoRsp;
import messages.activity.user_generated_content.UgcType;
import messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;

public class PacketGetUgcBriefInfoRsp extends BaseTypedPacket<GetUgcBriefInfoRsp> {

	public PacketGetUgcBriefInfoRsp(RetcodeOuterClass.Retcode ret, UgcType ugcType) {
		super(new GetUgcBriefInfoRsp());

        proto.setRetcode(ret.getNumber());
        proto.setUgcType(ugcType);
	}

    public PacketGetUgcBriefInfoRsp(UgcMusicBriefInfo briefInfo, UgcType ugcType) {
        super(new GetUgcBriefInfoRsp());

        proto.setBrief(new GetUgcBriefInfoRsp.Brief.MusicBriefInfo(briefInfo));
        proto.setUgcType(ugcType);
    }


}
