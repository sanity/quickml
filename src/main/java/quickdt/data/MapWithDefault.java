package quickdt.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisreeves on 8/12/14.
 */
public class MapWithDefault<K,V> implements Map<K,V> {

    private final Map<K,V> map;
    private final V defaultValue;

    public MapWithDefault(Map<K, V> map, V defaultValue) {
        this.map = map;
        this.defaultValue = defaultValue;
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
    public V get(Object key) {
        V value = map.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public V put(K key, V value) {
        return map.put(key,value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = map.values();
        values.add(defaultValue);
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapWithDefault that = (MapWithDefault) o;

        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        if (map != null ? !map.equals(that.map) : that.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = map != null ? map.hashCode() : 0;
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }
}
