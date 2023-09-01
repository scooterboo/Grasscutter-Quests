package emu.grasscutter.scripts.data;

import emu.grasscutter.game.props.EntityIdType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class SceneGadget extends SceneObject{
    private int gadget_id;
    private int state;
    private int point_type;
    private SceneBossChest boss_chest;
    private int chest_drop_id;
    private int interact_id;
    private boolean isOneoff;
    private int draft_id;
    private int route_id;
    private boolean start_route = true;
    private boolean is_use_point_array  = false;
    private boolean persistent = false;
    private boolean showcutscene;
    private Explore explore;
    private List<Integer> arguments;

    public void setIsOneoff(boolean isOneoff) {
        this.isOneoff = isOneoff;
    }

    @Override
    public EntityIdType getType() {
        return EntityIdType.GADGET;
    }
}
