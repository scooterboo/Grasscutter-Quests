package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.home.FurnitureCurModuleArrangeCountNotify;

public class PacketFurnitureCurModuleArrangeCountNotify extends BaseTypedPacket<FurnitureCurModuleArrangeCountNotify> {
	public PacketFurnitureCurModuleArrangeCountNotify() {
        super(new FurnitureCurModuleArrangeCountNotify());
		// TODO
	}
}
