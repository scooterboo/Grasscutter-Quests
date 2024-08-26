package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Position;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.PlaySoundType;
import org.anime_game_servers.multi_proto.gi.messages.unsorted.second.ScenePlayerSoundNotify;

import java.util.Objects;

public class PacketScenePlayerSoundNotify extends BaseTypedPacket<ScenePlayerSoundNotify> {
	public PacketScenePlayerSoundNotify(Position playPosition, String soundName, int playType) {
        super(new ScenePlayerSoundNotify(), true);
        if (!Objects.equals(playPosition, null)) {
            proto.setPlayPos(playPosition.toProto());
        }
        if (!Objects.equals(soundName, null)) {
            proto.setSoundName(soundName);
        }
        proto.setPlayType(PlaySoundType.getEntries().get(playType));
	}
}
