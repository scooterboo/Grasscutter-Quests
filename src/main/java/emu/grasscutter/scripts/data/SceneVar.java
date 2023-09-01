package emu.grasscutter.scripts.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class SceneVar {
    private int configId;
    private String name;
    private int value;
    private boolean no_refresh;
}
