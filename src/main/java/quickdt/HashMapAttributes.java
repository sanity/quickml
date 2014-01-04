package quickdt;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class HashMapAttributes implements Attributes {

    private HashMap<String, Serializable> delegatedHashMap = Maps.newHashMap();

	public static Attributes create(final Serializable... inputs) {
		final HashMapAttributes a = new HashMapAttributes();
		for (int x = 0; x < inputs.length; x += 2) {
			a.put((String) inputs[x], inputs[x + 1]);
		}
		return a;
	}

    @Override
    public int size() {
        return delegatedHashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return delegatedHashMap.isEmpty();
    }

    @Override
    public Serializable get(final Object key) {
        return delegatedHashMap.get(key);
    }

    @Override
    public boolean containsKey(final Object key) {
        return delegatedHashMap.containsKey(key);
    }

    @Override
    public Serializable put(final String key, final Serializable value) {
        return delegatedHashMap.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Serializable> m) {
        delegatedHashMap.putAll(m);
    }

    @Override
    public Serializable remove(final Object key) {
        return delegatedHashMap.remove(key);
    }

    @Override
    public void clear() {
        delegatedHashMap.clear();
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegatedHashMap.containsValue(value);
    }

    @Override
    public Object clone() {
        return delegatedHashMap.clone();
    }

    @Override
    public Set<String> keySet() {
        return delegatedHashMap.keySet();
    }

    @Override
    public Collection<Serializable> values() {
        return delegatedHashMap.values();
    }

    @Override
    public Set<Entry<String,Serializable>> entrySet() {
        return delegatedHashMap.entrySet();
    }

    @Override
    public Instance classification(final Serializable cls) {
        return new Instance(this, cls);
    }
}
