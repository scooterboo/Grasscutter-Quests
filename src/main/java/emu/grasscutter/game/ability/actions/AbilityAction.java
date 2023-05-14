package emu.grasscutter.game.ability.actions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;

@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityAction {
    AbilityModifierAction.Type value();
}
