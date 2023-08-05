package emu.grasscutter.game.entity.interfaces;

import emu.grasscutter.game.ability.AbilityManager;

import javax.annotation.Nullable;
import java.util.Collection;

public interface BaseAbilityEntity<T> {
    public void initAbilities();
    @Nullable
    public Collection<T> getAbilityData();
    AbilityManager getAbilityTargetManager();

}
