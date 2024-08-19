package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameStartRsp;

public class PacketMusicGameStartRsp extends BaseTypedPacket<MusicGameStartRsp> {

	public PacketMusicGameStartRsp(int musicBasicId, long musicShareId) {
        super(new MusicGameStartRsp());
        proto.setMusicBasicId(musicBasicId);
        proto.setUgcGuid(musicShareId);
	}
}
