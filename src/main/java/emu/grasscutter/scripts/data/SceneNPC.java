package emu.grasscutter.scripts.data;

import emu.grasscutter.game.props.EntityIdType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class SceneNPC extends SceneObject{
	private int npc_id;

    @Override
    public EntityIdType getType() {
        return EntityIdType.NPC;
    }
}
