package emu.grasscutter.game.entity.interfaces;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.entity.GameEntity;
import lombok.val;

public interface StringAbilityEntity extends BaseAbilityEntity<String>{
    @Override
    default void initAbilities() {
        val abilityData = getAbilityData();
        if (abilityData == null || abilityData.isEmpty()) return;

        val abilityManager = getAbilityTargetManager();

        for (val entityAbilityNames : abilityData) {
            val data = GameData.getAbilityData(entityAbilityNames);
            if (data != null) {
                abilityManager.addAbilityToEntity((GameEntity) this, data);
            }
        }
    }
}
