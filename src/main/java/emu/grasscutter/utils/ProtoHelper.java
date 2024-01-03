package emu.grasscutter.utils;

import emu.grasscutter.game.props.PlayerProperty;
import messages.general.PropValue;

public final class ProtoHelper {
	public static PropValue newPropValue(PlayerProperty key, int value) {
		return new PropValue(key.getId(), value, new PropValue.Value.Ival(value));
	}
}
