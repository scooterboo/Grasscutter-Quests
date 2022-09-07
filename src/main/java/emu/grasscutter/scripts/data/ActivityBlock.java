package emu.grasscutter.scripts.data;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.ScriptLoader;
import emu.grasscutter.utils.Position;
import lombok.Setter;
import lombok.ToString;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import static emu.grasscutter.config.Configuration.SCRIPT;

import java.util.Map;
import java.util.stream.Collectors;

@ToString
@Setter
public class ActivityBlock extends BaseBlock<ActivityGroup>{
    public int activityId;

    public boolean contains(Position pos) {
        return true;/*pos.getX() <= this.max.getX() && pos.getX() >= this.min.getX() &&
                pos.getZ() <= this.max.getZ() && pos.getZ() >= this.min.getZ();*/
    }

    @Override
    protected Class<ActivityGroup> getGroupType() {
        return ActivityGroup.class;
    }

    @Override
    protected String getScriptPath(int activityId) {
        return "Activity/" + activityId + "/activity" + activityId + "_block" + this.id + "." + ScriptLoader.getScriptType();
    }

    public ActivityBlock load(int activityId, Bindings bindings) {
        if (this.isLoaded()) {
            return this;
        }
        this.activityId = activityId;
        this.sceneId = 3;
        ActivityBlock result = (ActivityBlock) super.load(activityId, bindings);
        if(result!=null){
            this.groups.values().forEach(g -> g.setActivityId(activityId));
        }
        return result;
    }
}
