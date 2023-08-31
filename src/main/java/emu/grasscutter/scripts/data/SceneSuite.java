package emu.grasscutter.scripts.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import lombok.ToString;
import lombok.val;

@ToString
@Setter
public class SceneSuite {
    // make it refer the default empty list to avoid NPE caused by some group
	public List<Integer> monsters = List.of();
	public List<Integer> gadgets = List.of();
	public List<String> triggers = List.of();
    public List<Integer> regions = List.of();
    public int rand_weight;

    public boolean ban_refresh = false;

	public transient List<SceneMonster> sceneMonsters = List.of();
	public transient List<SceneGadget> sceneGadgets = List.of();
	public transient List<SceneTrigger> sceneTriggers = List.of();
    public transient List<SceneRegion> sceneRegions = List.of();

    public void init(SceneGroup sceneGroup) {
        val monsters = sceneGroup.getMonsters();
        if(monsters != null){
            this.sceneMonsters = new ArrayList<>(
                this.monsters.stream()
                    .filter(monsters::containsKey)
                    .map(monsters::get)
                    .toList()
            );
        }

        val gadgets = sceneGroup.getGadgets();
        if(gadgets != null){
            this.sceneGadgets = new ArrayList<>(
                this.gadgets.stream()
                    .filter(gadgets::containsKey)
                    .map(gadgets::get)
                    .toList()
            );
        }

        val triggers = sceneGroup.getTriggers();
        if(triggers != null) {
            this.sceneTriggers = new ArrayList<>(
                this.triggers.stream()
                    .filter(triggers::containsKey)
                    .map(triggers::get)
                    .toList()
            );
        }
        val regions = sceneGroup.getRegions();
        if(regions != null) {
            this.sceneRegions = new ArrayList<>(
                this.regions.stream()
                    .filter(regions::containsKey)
                    .map(regions::get)
                    .toList()
            );
        }

    }
}
