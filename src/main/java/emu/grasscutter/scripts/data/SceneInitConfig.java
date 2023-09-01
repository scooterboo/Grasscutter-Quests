package emu.grasscutter.scripts.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class SceneInitConfig {
	private int suite;
    private int end_suite;
    private int io_type ;
    private boolean rand_suite;
}
