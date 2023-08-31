package emu.grasscutter.game.inventory;

import dev.morphia.annotations.Entity;

@Entity
public class TowerItem extends GameItem{

    public TowerItem(GameItem original) {
        super(original.getItemId());
        this.itemId = original.itemId;
        this.count = original.count;
        this.itemData = original.itemData;
        this.level = original.level;
        this.exp = original.exp;
        this.totalExp = original.totalExp;
        this.promoteLevel = original.promoteLevel;
        this.locked = original.locked;
        this.refinement = original.refinement;
        this.mainPropId = original.mainPropId;
        this.equipCharacter = original.equipCharacter;

        this.affixes.clear(); this.affixes.addAll(original.affixes);
        this.appendPropIdList.clear(); this.appendPropIdList.addAll(original.appendPropIdList);
    }

    public TowerItem copy() {
        return new TowerItem(this);
    }

    @Override
    public void save() {

    }
}
