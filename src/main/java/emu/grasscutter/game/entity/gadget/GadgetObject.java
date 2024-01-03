package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import messages.gadget.GadgetInteractReq;
import messages.scene.entity.SceneGadgetInfo;

public class GadgetObject extends GadgetContent{
    public GadgetObject(EntityGadget gadget) {
        super(gadget);
    }

    @Override
    public boolean onInteract(Player player, GadgetInteractReq req) {
        return false;
    }

    @Override
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {

    }
}
