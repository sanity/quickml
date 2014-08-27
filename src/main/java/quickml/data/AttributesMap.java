package quickml.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by alexanderhawk on 8/26/14.
 */
public class AttributesMap implements Map<String, Serializable>, Serializable {

    private final Map<String, Serializable> map;

    public AttributesMap(Map<String, Serializable> map) {
        this.map = map;
    }

    public static AttributesMap newHashMap() {
        return new AttributesMap(new HashMap<String, Serializable>());
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Serializable get(Object key) {
        return map.get(key);
    }

    @Override
    public Serializable put(String key, Serializable value) {
        return map.put(key, value);
    }

    @Override
    public Serializable remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Serializable> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Serializable> values() {
        Collection<Serializable> values = map.values();
        return values;
    }

    @Override
    public Set<Entry<String, Serializable>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributesMap that = (AttributesMap) o;

        if (map != null ? !map.equals(that.map) : that.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = map != null ? map.hashCode() : 0;
        result = 31 * result;
        return result;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}



