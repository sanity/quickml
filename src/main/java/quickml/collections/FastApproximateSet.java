package quickml.collections;

import net.openhft.koloboke.collect.set.hash.HashIntSet;
import net.openhft.koloboke.collect.set.hash.HashIntSets;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 9/10/14.
 */
public class FastApproximateSet<V> implements Set<V>, Serializable {
    private static final long serialVersionUID = 6119589774706218057L;
    private final Set<V> slowSet;
    private final HashIntSet fastSet;

    public FastApproximateSet() {
        this.slowSet = new HashSet<>();
        this.fastSet = HashIntSets.newMutableSet();
    }

    public FastApproximateSet(final Set<V> slowSet) {
        this.slowSet = slowSet;
        this.fastSet = HashIntSets.newMutableSet();
        for (V o : slowSet) {
            fastSet.add(o.hashCode());
        }
    }

    @Override
    public int size() {
        return slowSet.size();
    }

    @Override
    public boolean isEmpty() {
        return slowSet.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return slowSet.contains(o);
    }

    public boolean probablyContains(V o) {
        return fastSet.contains(o.hashCode());
    }

    @Override
    public Iterator<V> iterator() {
        return slowSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return slowSet.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return slowSet.toArray(a);
    }

    @Override
    public boolean add(final V v) {
        fastSet.add(v.hashCode());
        return slowSet.add(v);
    }

    @Override
    public boolean remove(final Object o) {
        fastSet.removeInt(o.hashCode());
        return slowSet.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c){
        return slowSet.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends V> c) {
        for (V o : c) {
            fastSet.add(o.hashCode());
        }
        return slowSet.addAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        fastSet.clear();
        slowSet.clear();
    }
}
