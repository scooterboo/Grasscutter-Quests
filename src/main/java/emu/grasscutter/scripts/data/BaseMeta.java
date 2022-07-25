package emu.grasscutter.scripts.data;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.ScriptLoader;
import lombok.Setter;
import lombok.ToString;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import static emu.grasscutter.config.Configuration.SCRIPT;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@Setter
public abstract class BaseMeta<T extends BaseBlock<?>> {

    public Map<Integer, T> blocks;
    public Bindings context;
    public String metaType = "BaseMeta";

    protected abstract String getScriptPath(int id);
    protected abstract void loadFromLua();


    public <Y extends BaseMeta> Y load(int id) {
        // Get compiled script if cached
        CompiledScript cs = ScriptLoader.getScriptByPath(
                SCRIPT(getScriptPath(id)));

        if (cs == null) {
            Grasscutter.getLogger().warn("No script found for {} {}", metaType, id);
            return null;
        }

        // Create bindings
        this.context = ScriptLoader.getEngine().createBindings();

        // Eval script
        try {
            cs.eval(this.context);

            loadFromLua();


        } catch (ScriptException exception) {
            Grasscutter.getLogger().error("An error occurred while running a script.", exception);
            return null;
        }
        Grasscutter.getLogger().debug("Successfully loaded metadata in {} {}.", metaType, id);
        return (Y)this;
    }


}
