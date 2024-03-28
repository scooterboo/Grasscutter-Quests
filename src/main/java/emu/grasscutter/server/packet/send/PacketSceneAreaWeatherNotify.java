package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.weather.SceneAreaWeatherNotify;

public class PacketSceneAreaWeatherNotify extends BaseTypedPacket<SceneAreaWeatherNotify> {

	public PacketSceneAreaWeatherNotify(Player player) {
		super(new SceneAreaWeatherNotify());

		proto.setWeatherAreaId(player.getWeatherId());
        proto.setClimateType(player.getClimate().getValue());
	}
}
