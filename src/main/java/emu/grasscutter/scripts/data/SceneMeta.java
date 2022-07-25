package emu.grasscutter.scripts.data;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.ScriptLoader;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Setter
public class SceneMeta extends BaseMeta {

    public SceneConfig config;
    public RTree<SceneBlock, Geometry> sceneBlockIndex;

    SceneMeta() {
        super();
        this.metaType = "scene";
    }

    public static SceneMeta of(int sceneId) {
        return (SceneMeta) new SceneMeta().load(sceneId);
    }

    @Override
    protected String getScriptPath(int sceneId) {
        return "Scene/" + sceneId + "/scene" + sceneId + "." + ScriptLoader.getScriptType();
    }

    @Override
    protected void loadFromLua(){
        this.config = ScriptLoader.getSerializer().toObject(SceneConfig.class, this.context.get("scene_config"));
        // TODO optimize later
        // Create blocks
        List<Integer> blockIds = ScriptLoader.getSerializer().toList(Integer.class, this.context.get("blocks"));
        List<SceneBlock> blocks = ScriptLoader.getSerializer().toList(SceneBlock.class, this.context.get("block_rects"));

        for (int i = 0; i < blocks.size(); i++) {
            SceneBlock block = blocks.get(i);
            block.id = blockIds.get(i);
        }

        this.blocks = blocks.stream().collect(Collectors.toMap(b -> b.id, b -> b));
        this.sceneBlockIndex = SceneIndexManager.buildIndex(2, blocks, SceneBlock::toRectangle);
    }
}
