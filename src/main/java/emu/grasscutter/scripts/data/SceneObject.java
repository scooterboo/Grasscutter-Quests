package emu.grasscutter.scripts.data;

import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.EntityType;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public abstract class SceneObject {
    protected int level;
    protected int config_id;
    protected int area_id;
    protected int vision_level = 0;
    protected int mark_flag;
    protected String drop_tag;
    protected int guest_ban_drop;
    protected int oneoff_reset_version;
    protected int sight_group_index;
    // server_global_value_config, might be group only

    protected Position pos;
    protected Position rot;
    /**
     * not set by lua
     */
    protected transient SceneGroup group;

    public abstract EntityIdType getType();
}
