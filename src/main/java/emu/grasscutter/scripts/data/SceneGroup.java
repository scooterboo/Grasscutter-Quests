package emu.grasscutter.scripts.data;

import emu.grasscutter.scripts.ScriptLoader;
import lombok.Setter;
import lombok.ToString;

import static emu.grasscutter.config.Configuration.SCRIPT;

@ToString
@Setter
public class SceneGroup extends BaseGroup{

    public static SceneGroup of(int groupId) {
        var group = new SceneGroup();
        group.id = groupId;
        return group;
    }

    @Override
    public String getScriptPath(int sceneId) {
        return "Scene/" + sceneId + "/scene" + sceneId + "_group" + this.id + "." + ScriptLoader.getScriptType();
    }

}
