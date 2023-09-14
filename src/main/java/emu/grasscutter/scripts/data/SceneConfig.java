package emu.grasscutter.scripts.data;

import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class SceneConfig {
	private Position vision_anchor;
    private Position born_pos;
    private Position born_rot;
    private Position begin_pos;
    private Position size;
    private float die_y;
}
