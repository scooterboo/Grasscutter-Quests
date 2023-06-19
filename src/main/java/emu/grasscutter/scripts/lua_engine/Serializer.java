package emu.grasscutter.scripts.lua_engine;

import java.util.List;
import java.util.Map;


public interface Serializer {

    public <T> List<T> toList(Class<T> type, Object obj);

    public <T> T toObject(Class<T> type, Object obj);

    public <T> Map<String,T> toMap(Class<T> type, Object obj);
}
