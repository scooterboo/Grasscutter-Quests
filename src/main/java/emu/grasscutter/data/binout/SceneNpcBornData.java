package emu.grasscutter.data.binout;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SceneNpcBornData {
    int sceneId;
    List<SceneNpcBornEntry> bornPosList;

    /**
     * Spatial Index For NPC
     */
    transient RTree<SceneNpcBornEntry, Geometry> index;

    /**
     * npc groups
     */
    transient Map<Integer, SceneGroup> groups = new ConcurrentHashMap<>();
}
