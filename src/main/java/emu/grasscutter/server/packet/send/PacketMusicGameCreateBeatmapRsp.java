package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.proto.RetcodeOuterClass;
import messages.activity.user_generated_content.SaveUgcRsp;
import messages.activity.user_generated_content.UgcType;

public class PacketMusicGameCreateBeatmapRsp extends BaseTypedPacket<SaveUgcRsp> {

	public PacketMusicGameCreateBeatmapRsp(long musicShareId, UgcType ugcType) {
		super(new SaveUgcRsp(musicShareId, ugcType));
	}

	public PacketMusicGameCreateBeatmapRsp(RetcodeOuterClass.Retcode retCode, UgcType ugcType) {
		super(new SaveUgcRsp());

        proto.setRetcode(retCode.getNumber());
        proto.setUgcType(ugcType);
	}
}
