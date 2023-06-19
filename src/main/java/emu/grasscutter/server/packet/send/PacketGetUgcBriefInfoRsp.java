package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.net.proto.RetcodeOuterClass;
import emu.grasscutter.server.game.GameSession;
import messages.activity.user_generated_content.GetUgcBriefInfoRsp;
import messages.activity.user_generated_content.UgcType;
import messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;

public class PacketGetUgcBriefInfoRsp extends BaseTypedPackage<GetUgcBriefInfoRsp> {

	public PacketGetUgcBriefInfoRsp(RetcodeOuterClass.Retcode ret, UgcType ugcType) {
		super(new GetUgcBriefInfoRsp());

        proto.setRetcode(ret.getNumber());
        proto.setUgcType(ugcType);
	}

    public PacketGetUgcBriefInfoRsp(UgcMusicBriefInfo briefInfo, UgcType ugcType) {
        super(new GetUgcBriefInfoRsp());

        proto.setBrief(new GetUgcBriefInfoRsp.Brief.UgcMusicBriefInfo(briefInfo));
        proto.setUgcType(ugcType);
    }


}
