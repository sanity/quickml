package quickdt;

import com.google.common.collect.Maps;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ClassificationCounter implements Serializable {
    private static final long serialVersionUID = -6821237234748044623L;
    private final Map<Serializable, Double> counts = Maps.newHashMap();

	private double total = 0;

    public static ClassificationCounter merge(ClassificationCounter a, ClassificationCounter b) {
        ClassificationCounter newCC = new ClassificationCounter();
        newCC.counts.putAll(a.counts);
        for (Entry<Serializable, Double> e : b.counts.entrySet()) {
            Double existingCountVal = newCC.counts.get(e.getKey());
            if (existingCountVal == null) {
                existingCountVal = 0.0;
            }
            newCC.counts.put(e.getKey(), existingCountVal+e.getValue());
        }
        newCC.total = a.total+b.total;
        return newCC;
    }

	public static Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> countAllByAttributeValues(
			final Iterable<? extends AbstractInstance> instances, final String attribute) {
		final Map<Serializable, ClassificationCounter> result = Maps.newHashMap();
		final ClassificationCounter totals = new ClassificationCounter();
		for (final AbstractInstance i : instances) {
			final Serializable attrVal = i.getAttributes().get(attribute);
			if (attrVal != null) {
				ClassificationCounter cc = result.get(attrVal);
				if (cc == null) {
					cc = new ClassificationCounter();
					result.put(attrVal, cc);
				}
				cc.addClassification(i.getClassification(), i.getWeight());
				totals.addClassification(i.getClassification(), i.getWeight());
			}
		}
		return Pair.with(totals, result);
	}

    public Map<Serializable, Double> getCounts() {
        return Collections.unmodifiableMap(counts);
    }


	public static ClassificationCounter countAll(final Iterable<? extends AbstractInstance> instances) {
		final ClassificationCounter result = new ClassificationCounter();
		for (final AbstractInstance i : instances) {
			result.addClassification(i.getClassification(), i.getWeight());
		}
		return result;
	}

	public void addClassification(final Serializable classification, double weight) {
		Double c = counts.get(classification);
		if (c == null) {
			c = 0.0;
		}
		total+= weight;
		counts.put(classification, c + weight);
	}

	public double getCount(final Serializable classification) {
		final Double c = counts.get(classification);
		if (c == null)
			return 0.0;
		else
			return c;
	}

	public Set<Serializable> allClassifications() {
		return counts.keySet();
	}

	public ClassificationCounter add(final ClassificationCounter other) {
		final ClassificationCounter result = new ClassificationCounter();
		result.counts.putAll(counts);
		for (final Entry<Serializable, Double> e : other.counts.entrySet()) {
			result.counts.put(e.getKey(), getCount(e.getKey()) + e.getValue());
		}
		result.total = total + other.total;
		return result;
	}

	public ClassificationCounter subtract(final ClassificationCounter other) {
		final ClassificationCounter result = new ClassificationCounter();
		for (final Entry<Serializable, Double> e : counts.entrySet()) {
			result.counts.put(e.getKey(), e.getValue() - other.getCount(e.getKey()));
		}
		result.total = total - other.total;
		return result;
	}

	public double getTotal() {
		return total;
	}

	public Pair<Serializable, Double> mostPopular() {
		Entry<Serializable, Double> best = null;
		for (final Entry<Serializable, Double> e : counts.entrySet()) {
			if (best == null || e.getValue() > best.getValue()) {
				best = e;
			}
		}
		return Pair.with(best.getKey(), best.getValue());
	}
}
