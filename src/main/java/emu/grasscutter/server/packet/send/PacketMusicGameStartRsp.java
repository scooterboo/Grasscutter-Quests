package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.activity.music_game.MusicGameStartRsp;

public class PacketMusicGameStartRsp extends BaseTypedPacket<MusicGameStartRsp> {

	public PacketMusicGameStartRsp(int musicBasicId, long musicShareId) {
		super(new MusicGameStartRsp(musicBasicId, musicShareId));
	}
}
