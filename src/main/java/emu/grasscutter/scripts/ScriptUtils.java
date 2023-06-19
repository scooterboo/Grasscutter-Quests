package emu.grasscutter.scripts;

import java.util.HashMap;

import emu.grasscutter.scripts.lua_engine.LuaEngine;
import emu.grasscutter.scripts.lua_engine.LuaTable;
import emu.grasscutter.utils.Position;
import lombok.val;
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

    public static LuaTable posToLua(Position position, LuaEngine engine){
        var result = engine.createTable();
        if(position != null){
            result.set("x", position.getX());
            result.set("y", position.getY());
            result.set("z", position.getZ());
        } else {
            result.set("x", 0);
            result.set("y", 0);
            result.set("z", 0);
        }

        return result;
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
