package emu.grasscutter.scripts;

import java.util.HashMap;

import emu.grasscutter.utils.Position;
import lombok.val;
import org.anime_game_servers.lua.engine.LuaTable;
import org.luaj.vm2.LuaValue;

public class ScriptUtils {

	public static HashMap<Object, Object> toMap(org.luaj.vm2.LuaTable table) {
		HashMap<Object, Object> map = new HashMap<>();
		LuaValue[] rootKeys = table.keys();
		for (LuaValue k : rootKeys) {
			if (table.get(k).istable()) {
				map.put(k, toMap(table.get(k).checktable()));
			} else {
		    	map.put(k, table.get(k));
			}
		}
		return map;
	}

    public static Position luaToPos(LuaTable position){
        val result = new Position();
        if(position != null){
            result.setX(position.optInt("x", 0));
            result.setY(position.optInt("y", 0));
            result.setZ(position.optInt("z", 0));
        }

        return result;
    }
}
