package emu.grasscutter.game.dungeons.challenge.trigger;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketChallengeDataNotify;
import lombok.Getter;

import java.util.Optional;

@Getter
public class GuardTrigger extends ChallengeTrigger {
    private int lastSendPercent = 100;

    public GuardTrigger(int paramIndex, int entityToProtectCFGId){
        super(paramIndex, entityToProtectCFGId);
    }

    @Override
    public void onBegin(WorldChallenge challenge) {
        challenge.getScene().broadcastPacket(new PacketChallengeDataNotify(challenge, getParamIndex(), this.lastSendPercent));
    }

    @Override
    public void onGadgetDamage(WorldChallenge challenge, EntityGadget gadget) {
        if(gadget.getConfigId() != getGoal().get()) return;

        float curHp = Optional.ofNullable(gadget.getFightProperties())
            .map(prop -> prop.get(FightProperty.FIGHT_PROP_CUR_HP.getId())).orElse(0f);
        float maxHp = Optional.ofNullable(gadget.getFightProperties())
            .map(prop -> prop.get(FightProperty.FIGHT_PROP_MAX_HP.getId())).orElse(0f);
        float percent =  (curHp / maxHp) * 100;

        if(percent != this.lastSendPercent) {
            this.lastSendPercent += (int) percent;
            onBegin(challenge);
        }

        if(percent <= 0) challenge.fail();
    }
}
