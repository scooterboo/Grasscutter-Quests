package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.GadgetInteractReqOuterClass;
import emu.grasscutter.net.proto.SceneGadgetInfoOuterClass;
import emu.grasscutter.net.proto.ScreenInfoOuterClass;
import lombok.val;

public class GadgetScreen extends GadgetContent {
    public GadgetScreen(EntityGadget gadget) {
        super(gadget);
    }

    @Override
    public boolean onInteract(Player player, GadgetInteractReqOuterClass.GadgetInteractReq req) {
        return false;
    }

    @Override
    public void onBuildProto(SceneGadgetInfoOuterClass.SceneGadgetInfo.Builder gadgetInfo) {
        val screen = ScreenInfoOuterClass.ScreenInfo.newBuilder()
            //.setProjectorEntityId(getGadget().getScene().getEntityByConfigId(178001, getGadget().getGroupId()).getId())
            .setLiveId(1);

        gadgetInfo.setScreenInfo(screen);
    }
}
