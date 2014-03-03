package quickdt.collections;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 3/2/14.
 */
public class ValueSummingMap<K> implements Map<K, Number>, Serializable {
    private static final long serialVersionUID = 582080010331916162L;
    private volatile double sumOfValues = 0;

    private HashMap<K, Number> delegateMap = new HashMap<K, Number>();

    @Override
    public int size() {
        return delegateMap.size();
    }

    public double getSumOfValues() {
        return sumOfValues;
    }

    @Override
    public boolean isEmpty() {
        return delegateMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return delegateMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegateMap.containsValue(value);
    }

    @Override
    public Number get(final Object key) {
        return delegateMap.get(key);
    }

    @Override
    public Number put(final K key, final Number value) {
        Number oldValue = delegateMap.put(key, value);
        if (oldValue != null) {
            sumOfValues = (sumOfValues - oldValue.doubleValue()) + value.doubleValue();
        } else {
            sumOfValues += value.doubleValue();
        }
        return oldValue;
    }

    public void addToValue(final K key, final double toAdd) {
        Number oldValue = get(key);
        if (oldValue == null) {
            oldValue = new Double(0.0);
        }
        delegateMap.put(key, oldValue.doubleValue() + toAdd);
        sumOfValues += toAdd;
    }

    @Override
    public Number remove(final Object key) {
        Number removedValue = delegateMap.remove(key);
        if (removedValue != null) {
            sumOfValues -= removedValue.doubleValue();
        }
        return removedValue;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends Number> m) {
        for (Entry<? extends K, ? extends Number> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        sumOfValues = 0;
        delegateMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegateMap.keySet();
    }

    @Override
    public Collection<Number> values() {
        return delegateMap.values();
    }

    @Override
    public Set<Entry<K, Number>> entrySet() {
        return delegateMap.entrySet();
    }
}
