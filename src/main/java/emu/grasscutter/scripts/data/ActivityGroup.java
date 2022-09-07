package emu.grasscutter.scripts.data;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.ScriptLoader;
import emu.grasscutter.utils.Position;
import lombok.Setter;
import lombok.ToString;
import org.luaj.vm2.LuaValue;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import static emu.grasscutter.config.Configuration.SCRIPT;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ToString
@Setter
public class ActivityGroup extends BaseGroup {
    private int activityId;
    public static ActivityGroup of(int groupId) {
        var group = new ActivityGroup();
        group.id = groupId;
        return group;
    }

    @Override
    public String getScriptPath(int sceneId) {
        return "Activity/" + activityId + "/activity" + activityId + "_group" + this.id + "." + ScriptLoader.getScriptType();
    }

    @Override
    public synchronized BaseGroup load(int parentId) {
        return super.load(parentId);
    }
}
