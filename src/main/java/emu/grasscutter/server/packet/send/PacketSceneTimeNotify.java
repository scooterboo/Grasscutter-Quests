package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.scene.SceneTimeNotify;

public class PacketSceneTimeNotify extends BaseTypedPacket<SceneTimeNotify> {

    public PacketSceneTimeNotify(Player player) {
        this(player.getScene());
    }

    public PacketSceneTimeNotify(Scene scene) {
        super(new SceneTimeNotify(scene.getId(), scene.isPaused(), scene.getSceneTime()));
    }
}
