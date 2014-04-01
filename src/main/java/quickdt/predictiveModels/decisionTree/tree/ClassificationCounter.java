package quickdt.predictiveModels.decisionTree.tree;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import quickdt.collections.ValueSummingMap;
import quickdt.data.AbstractInstance;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ClassificationCounter implements Serializable {
    private static final long serialVersionUID = -6821237234748044623L;
    private final ValueSummingMap<Serializable> counts = new ValueSummingMap<Serializable>();

    public static ClassificationCounter merge(ClassificationCounter a, ClassificationCounter b) {
        ClassificationCounter newCC = new ClassificationCounter();
        newCC.counts.putAll(a.counts);
        for (Entry<Serializable, Number> e : b.counts.entrySet()) {
            newCC.counts.addToValue(e.getKey(), e.getValue().doubleValue());
        }
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
        Map<Serializable, Double> ret = Maps.newHashMap();
        for (Entry<Serializable, Number> serializableNumberEntry : counts.entrySet()) {
            ret.put(serializableNumberEntry.getKey(), serializableNumberEntry.getValue().doubleValue());
        }
        return ret;
    }


	public static ClassificationCounter countAll(final Iterable<? extends AbstractInstance> instances) {
		final ClassificationCounter result = new ClassificationCounter();
		for (final AbstractInstance i : instances) {
			result.addClassification(i.getClassification(), i.getWeight());
		}
		return result;
	}

	public void addClassification(final Serializable classification, double weight) {
		counts.addToValue(classification, weight);
	}

	public double getCount(final Serializable classification) {
		Number c = counts.get(classification);
        if (c == null) {
            return 0;
        } else {
            return c.doubleValue();
        }
	}

	public Set<Serializable> allClassifications() {
		return counts.keySet();
	}

	public ClassificationCounter add(final ClassificationCounter other) {
		final ClassificationCounter result = new ClassificationCounter();
		result.counts.putAll(counts);
		for (final Entry<Serializable, Number> e : other.counts.entrySet()) {
			result.counts.addToValue(e.getKey(), e.getValue().doubleValue());
		}
		return result;
	}

	public ClassificationCounter subtract(final ClassificationCounter other) {
		final ClassificationCounter result = new ClassificationCounter();
        result.counts.putAll(counts);
		for (final Entry<Serializable, Number> e : other.counts.entrySet()) {
			result.counts.addToValue(e.getKey(), -other.getCount(e.getKey()));
		}
		return result;
	}

	public double getTotal() {
		return counts.getSumOfValues();
	}

	public Pair<Serializable, Double> mostPopular() {
		Entry<Serializable, Number> best = null;
		for (final Entry<Serializable, Number> e : counts.entrySet()) {
			if (best == null || e.getValue().doubleValue() > best.getValue().doubleValue()) {
				best = e;
			}
		}
		return Pair.with(best.getKey(), best.getValue().doubleValue());
	}
}
