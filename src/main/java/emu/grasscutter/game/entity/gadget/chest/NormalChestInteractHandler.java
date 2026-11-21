package emu.grasscutter.game.entity.gadget.chest;

import emu.grasscutter.game.entity.gadget.content.GadgetChest;
import emu.grasscutter.game.player.Player;

public class NormalChestInteractHandler implements ChestInteractHandler {

    public NormalChestInteractHandler() {
    }

    @Override
    public boolean isTwoStep() {
        return false;
    }

    @Override
    public boolean onInteract(GadgetChest chest, Player player) {
        return true;
    }
}
