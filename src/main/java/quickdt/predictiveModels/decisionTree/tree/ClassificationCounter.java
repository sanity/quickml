package quickdt.predictiveModels.decisionTree.tree;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import quickdt.collections.ValueSummingMap;
import quickdt.data.AbstractInstance;
import static quickdt.predictiveModels.decisionTree.TreeBuilder.*;

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
			final Iterable<? extends AbstractInstance> instances, final String attribute, String splitAttribute, Serializable splitAttributeValue) {
		final Map<Serializable, ClassificationCounter> result = Maps.newHashMap();
		final ClassificationCounter totals = new ClassificationCounter();
        for (final AbstractInstance instance : instances) {
			final Serializable attrVal = instance.getAttributes().get(attribute);
            ClassificationCounter cc = null;
            boolean isAnAcceptableMissingValue = splitAttribute == null || splitAttributeValue == null || instance.getAttributes().get(splitAttribute).equals(splitAttributeValue);
            if (attrVal!=null)
                cc = result.get(attrVal);
            else if (isAnAcceptableMissingValue)
                cc = result.get(MISSING_VALUE);

            if (cc == null || isAnAcceptableMissingValue) {
					cc = new ClassificationCounter();
                    Serializable newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
					result.put(newKey, cc);
		    }
			cc.addClassification(instance.getClassification(), instance.getWeight());
			totals.addClassification(instance.getClassification(), instance.getWeight());
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
		for (final AbstractInstance instance : instances) {
			result.addClassification(instance.getClassification(), instance.getWeight());
		}
		return result;
	}

	public void addClassification(final Serializable classification, double weight) {
		counts.addToValue(classification, weight);
	}

	public double getCount(final Serializable classification) {
		Number count = counts.get(classification);
        if (count == null) {
            return 0;
        } else {
            return count.doubleValue();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassificationCounter that = (ClassificationCounter) o;

        if (!counts.equals(that.counts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return counts.hashCode();
    }
}
