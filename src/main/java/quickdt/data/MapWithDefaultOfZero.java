package quickdt.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisreeves on 8/12/14.
 */
public class MapWithDefaultOfZero implements Map<Serializable, Double> {

    private final Map<Serializable, Double> map;
    private final Double defaultValue = Double.valueOf(0);

    public MapWithDefaultOfZero(Map<Serializable, Double> map) {
        this.map = map;
    }

    public static MapWithDefaultOfZero newMap(){
        return new MapWithDefaultOfZero(new HashMap<Serializable, Double>());
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
    public Double get(Object key) {
        Double value = map.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public Double put(Serializable key, Double value) {
        return map.put(key,value);
    }

    @Override
    public Double remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Serializable, ? extends Double> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Serializable> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Double> values() {
        Collection<Double> values = map.values();
        values.add(defaultValue);
        return values;
    }

    @Override
    public Set<Entry<Serializable, Double>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapWithDefaultOfZero that = (MapWithDefaultOfZero) o;

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
