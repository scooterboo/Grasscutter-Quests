package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import emu.grasscutter.server.game.GameSession;
import messages.activity.user_generated_content.GetUgcReq;
import messages.activity.user_generated_content.GetUgcRsp;
import messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;
import messages.activity.user_generated_content.music_game.UgcMusicRecord;

public class PacketGetUgcRsp extends BaseTypedPackage<GetUgcRsp> {

    public PacketGetUgcRsp(UgcMusicBriefInfo briefInfo, UgcMusicRecord musicRecord, GetUgcReq req) {
        super(new GetUgcRsp(briefInfo.getUgcGuid(),
            req.getUgcType(),
            req.getUgcRecordUsage(),
            new GetUgcRsp.Record.UgcMusicRecord(musicRecord),
            new GetUgcRsp.Brief.UgcMusicBriefInfo(briefInfo)));
    }
    public PacketGetUgcRsp(Retcode errorCode, GetUgcReq req) {
        super( new GetUgcRsp(req.getUgcGuid(),
            req.getUgcType(),
            req.getUgcRecordUsage()));

        proto.setRetcode(errorCode.getNumber());
    }
}
