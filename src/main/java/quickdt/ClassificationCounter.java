package quickdt;

import com.google.common.collect.Maps;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ClassificationCounter implements Serializable {
	private final Map<Serializable, Integer> counts = Maps.newHashMap();

	private int total = 0;

	public static Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> countAllByAttributeValues(
			final Iterable<Instance> instances, final String attribute) {
		final Map<Serializable, ClassificationCounter> result = Maps.newHashMap();
		final ClassificationCounter totals = new ClassificationCounter();
		for (final Instance i : instances) {
			final Serializable attrVal = i.attributes.get(attribute);
			if (attrVal != null) {
				ClassificationCounter cc = result.get(attrVal);
				if (cc == null) {
					cc = new ClassificationCounter();
					result.put(attrVal, cc);
				}
				cc.addClassification(i.classification);
				totals.addClassification(i.classification);
			}
		}
		return Pair.with(totals, result);
	}

    public Map<Serializable, Integer> getCounts() {
        return Collections.unmodifiableMap(counts);
    }


	public static ClassificationCounter countAll(final Iterable<Instance> instances) {
		final ClassificationCounter result = new ClassificationCounter();
		for (final Instance i : instances) {
			result.addClassification(i.classification);
		}
		return result;
	}

	public void addClassification(final Serializable classification) {
		Integer c = counts.get(classification);
		if (c == null) {
			c = 0;
		}
		total++;
		counts.put(classification, c + 1);
	}

	public int getCount(final Serializable classification) {
		final Integer c = counts.get(classification);
		if (c == null)
			return 0;
		else
			return c;
	}

	public Set<Serializable> allClassifications() {
		return counts.keySet();
	}

	public ClassificationCounter add(final ClassificationCounter other) {
		final ClassificationCounter result = new ClassificationCounter();
		result.counts.putAll(counts);
		for (final Entry<Serializable, Integer> e : other.counts.entrySet()) {
			result.counts.put(e.getKey(), getCount(e.getKey()) + e.getValue());
		}
		result.total = total + other.total;
		return result;
	}

	public ClassificationCounter subtract(final ClassificationCounter other) {
		final ClassificationCounter result = new ClassificationCounter();
		for (final Entry<Serializable, Integer> e : counts.entrySet()) {
			result.counts.put(e.getKey(), e.getValue() - other.getCount(e.getKey()));
		}
		result.total = total - other.total;
		return result;
	}

	public int getTotal() {
		return total;
	}

	public Pair<Serializable, Integer> mostPopular() {
		Entry<Serializable, Integer> best = null;
		for (final Entry<Serializable, Integer> e : counts.entrySet()) {
			if (best == null || e.getValue() > best.getValue()) {
				best = e;
			}
		}
		return Pair.with(best.getKey(), best.getValue());
	}
}
