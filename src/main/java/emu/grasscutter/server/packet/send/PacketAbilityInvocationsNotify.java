package emu.grasscutter.server.packet.send;

import java.util.Collections;
import java.util.List;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.ability.AbilityInvocationsNotify;
import messages.ability.AbilityInvokeEntry;

public class PacketAbilityInvocationsNotify extends BaseTypedPacket<AbilityInvocationsNotify> {

	public PacketAbilityInvocationsNotify(AbilityInvokeEntry entry) {
		super(new AbilityInvocationsNotify(), true);

        proto.setInvokes(Collections.singletonList(entry));
	}

	public PacketAbilityInvocationsNotify(List<AbilityInvokeEntry> entries) {
		super(new AbilityInvocationsNotify(), true);

        proto.setInvokes(entries);
	}

}
