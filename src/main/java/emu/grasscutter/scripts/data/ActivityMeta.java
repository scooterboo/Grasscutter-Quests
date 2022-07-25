package emu.grasscutter.scripts.data;

import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.ScriptLoader;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@ToString
@Setter
public class ActivityMeta extends BaseMeta<ActivityBlock> {

    public SceneConfig config;
    public int id;
    public Map<Integer, ActivityGroup> groups;


    private transient boolean loaded; // Not an actual variable in the scripts either

    ActivityMeta(){
        super();
        this.metaType = "activity";
    }

    public static ActivityMeta of(int activityId) {
        return new ActivityMeta().load(activityId);
    }

    @Override
    protected String getScriptPath(int activityId) {
        return "Activity/" + activityId + "/activity" + activityId + "." + ScriptLoader.getScriptType();
    }

    @Override
    public ActivityMeta load(int id) {
        this.id = id;
        return super.load(id);
    }

    protected void loadFromLua(){
        // TODO optimize later
        // Create blocks
        List<Integer> blocksIDs = ScriptLoader.getSerializer().toList(Integer.class, this.context.get("blocks"));
        Set<ActivityBlock> blocks = new HashSet<>();
        for(int blockId : blocksIDs){
            ActivityBlock block = new ActivityBlock();
            block.id = blockId;
            block.activityId = id;
            blocks.add(block);
        }

        this.blocks = blocks.stream().collect(Collectors.toMap(b -> b.id, b -> b));
    }
}
