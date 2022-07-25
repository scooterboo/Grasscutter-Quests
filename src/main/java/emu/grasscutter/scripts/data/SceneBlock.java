package emu.grasscutter.scripts.data;

import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import emu.grasscutter.scripts.ScriptLoader;
import emu.grasscutter.utils.Position;
import lombok.Setter;
import lombok.ToString;

import javax.script.Bindings;

@ToString
@Setter
public class SceneBlock extends BaseBlock<SceneGroup>{
    public Position max;
    public Position min;

    public boolean contains(Position pos) {
        return 	pos.getX() <= this.max.getX() && pos.getX() >= this.min.getX() &&
                pos.getZ() <= this.max.getZ() && pos.getZ() >= this.min.getZ();
    }

    @Override
    protected String getScriptPath(int id) {
        return "Scene/" + sceneId + "/scene" + sceneId + "_block" + this.id + "." + ScriptLoader.getScriptType();
    }

    @Override
    protected Class<SceneGroup> getGroupType() {
        return SceneGroup.class;
    }

    public SceneBlock load(int sceneId, Bindings bindings) {
        if (this.isLoaded()) {
            return this;
        }
        this.sceneId = sceneId;
        return (SceneBlock) super.load(sceneId, bindings);
    }

    public Rectangle toRectangle() {
        return Rectangle.create(this.min.toXZDoubleArray(), this.max.toXZDoubleArray());
    }
}
