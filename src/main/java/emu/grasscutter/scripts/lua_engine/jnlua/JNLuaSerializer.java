package emu.grasscutter.scripts.lua_engine.jnlua;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.lua_engine.Serializer;
import org.terasology.jnlua.LuaValueProxy;
import org.terasology.jnlua.util.AbstractTableMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class JNLuaSerializer extends Serializer {

    private final ReentrantLock lock = new ReentrantLock();

    public static Integer getInt(Object value) {
        if (value instanceof Integer l) {
            return l.intValue();
        } else if (value instanceof Double d) {
            return d.intValue();
        }
        return 0;
    }

    public static Float getFloat(Object value) {
        if (value instanceof Double l) {
            return l.floatValue();
        } else if (value instanceof Integer l) {
            return l.floatValue();
        }
        return 0f;
    }

    // ...
    @Override
    public <T> List<T> toList(Class<T> type, Object obj) {
        return serializeList(type, (LuaValueProxy) obj);
    }

    @Override
    public <T> T toObject(Class<T> type, Object obj) {
        return serialize(type, (LuaValueProxy) obj);
    }

    @Override
    public <T> Map<String, T> toMap(Class<T> type, Object obj) {
        return serializeMap(type, (LuaValueProxy) obj);
    }

    private <T> T objectToClass(Class<T> type, Object value) {
        T object = null;

        if (value instanceof Integer) {
            object = (T) getInt(value);
        } else if (value instanceof Double) {
            object = (T) getFloat(value);
        } else if (value instanceof String) {
            object = (T) value;
        } else if (value instanceof Boolean) {
            object = (T) value;
        } else {
            object = serialize(type, (LuaValueProxy) value);
        }
        return object;
    }

    public <T> List<T> serializeList(Class<T> type, LuaValueProxy table) {
        List<T> list = new ArrayList<>();

        if (table == null) {
            return list;
        }

        var tableObj = (Map<String, Object>) table;
        try {
            for (var k : tableObj.entrySet()) {
                try {
                    var keyValue = k.getValue();

                    T object = objectToClass(type, keyValue);

                    if (object != null) {
                        list.add(object);
                    }
                } catch (Exception ex) {

                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("Exception serializing list", e);
        }

        return list;
    }

    public <T> T serialize(Class<T> type, LuaValueProxy table) {
        T object = null;

        if (type == List.class) {
            try {
                Class<T> listType = (Class<T>) type.getTypeParameters()[0].getClass();
                return (T) serializeList(listType, table);
            } catch (Exception e) {
                Grasscutter.getLogger().error("Exception serializing", e);
                return null;
            }
        }

        try {
            lock.lock();
            if (!methodAccessCache.containsKey(type)) {
                cacheType(type);
            }
            lock.unlock();
            var methodAccess = methodAccessCache.get(type);
            var fieldMetaMap = fieldMetaCache.get(type);

            object = (T) constructorCache.get(type).newInstance();

            if (table == null) {
                return object;
            }

            var tableObj = (AbstractTableMap<String>) table;
            for (var k : tableObj.entrySet()) {
                try {
                    var keyName = k.getKey();
                    if (!fieldMetaMap.containsKey(keyName)) {
                        continue;
                    }

                    var fieldMeta = fieldMetaMap.get(keyName);
                    var keyValue = k.getValue();
                    if (fieldMeta.getType().equals(float.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), getFloat(keyValue));
                    } else if (fieldMeta.getType().equals(double.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), (keyValue));
                    } else if (fieldMeta.getType().equals(int.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), getInt(keyValue));
                    } else if (fieldMeta.getType().equals(String.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue);
                    } else if (fieldMeta.getType().equals(boolean.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue);
                    } else if (fieldMeta.getType().equals(List.class)) {
                        List<T> listObj = tableObj.get(k.getKey(), List.class);
                        methodAccess.invoke(object, fieldMeta.getIndex(), listObj);
                    } else {
                        methodAccess.invoke(object, fieldMeta.getIndex(), serialize(fieldMeta.getType(), (LuaValueProxy) keyValue));
                        //methodAccess.invoke(object, fieldMeta.index, keyValue);
                    }
                } catch (Exception ex) {
                    Grasscutter.getLogger().error("Exception serializing", ex);
                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("Exception serializing", e);
        }

        return object;
    }

    public <T> Map<String, T> serializeMap(Class<T> type, LuaValueProxy table) {
        Map<String, T> map = new HashMap<>();

        if (table == null) {
            return map;
        }

        var tableObj = (Map<String, Object>) table;

        try {
            for (var k : tableObj.entrySet()) {
                try {
                    var keyValue = k.getValue();

                    T object = objectToClass(type, keyValue);

                    if (object != null) {
                        map.put(k.getKey(), object);
                    }
                } catch (Exception ex) {

                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("Exception serializing map", e);
        }

        return map;
    }
}
