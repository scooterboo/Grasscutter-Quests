package emu.grasscutter.scripts.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class SceneReplaceable {
    // TODO move to modifiable value in SceneGroupInstance
    @Setter
    private boolean value;
    private int version;
    private boolean new_bin_only;
}
