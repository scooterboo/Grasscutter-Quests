package emu.grasscutter.scripts;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.data.controller.EntityController;
import lombok.val;
import org.anime_game_servers.gi_lua.models.loader.GadgetScriptLoadParams;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static emu.grasscutter.utils.FileUtils.getScriptPath;

public class EntityControllerScriptManager {
    private static final Logger logger = Loggers.getScriptSystem();
    private static final Map<String, EntityController> gadgetController = new ConcurrentHashMap<>();

    public static void load(){
        cacheGadgetControllers();
    }

    private static void cacheGadgetControllers(){
        val scriptLoader = ScriptSystem.getScriptLoader();
        try (val stream = Files.newDirectoryStream(getScriptPath("Gadget/"), "*.lua")) {
            stream.forEach(path -> {
                val fileName = path.getFileName().toString();

                if(!fileName.endsWith(".lua"))
                    return;

                val controllerName = fileName.substring(0, fileName.length()-4);
                val scriptParams = new GadgetScriptLoadParams(fileName);
                scriptLoader.loadData(scriptParams, cs -> {
                    gadgetController.put(controllerName, new EntityController(cs));
                });
            });

            logger.info("Loaded {} gadget controllers", gadgetController.size());
        } catch (IOException e) {
            logger.error("Error loading gadget controller luas");
        }
    }


    public static EntityController getGadgetController(String name) {
        return gadgetController.get(name);
    }
}
