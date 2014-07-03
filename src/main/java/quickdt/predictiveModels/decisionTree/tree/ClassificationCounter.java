package quickdt.predictiveModels.decisionTree.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hadoop.util.hash.Hash;
import org.javatuples.Pair;
import quickdt.collections.ValueSummingMap;
import quickdt.data.AbstractInstance;
import static quickdt.predictiveModels.decisionTree.TreeBuilder.*;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;


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
            boolean acceptableMissingValue = attrVal ==null && isAnAcceptableMissingValue(instance, splitAttribute, splitAttributeValue);

            if (attrVal!=null)
                cc = result.get(attrVal);
            else if (acceptableMissingValue)
                cc = result.get(MISSING_VALUE);
            else
                continue;

            if (cc == null) {
                cc = new ClassificationCounter();
                Serializable newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
                result.put(newKey, cc);
            }
            cc.addClassification(instance.getClassification(), instance.getWeight());
            totals.addClassification(instance.getClassification(), instance.getWeight());
        }

        return Pair.with(totals, result);
    }

	public static Pair<ClassificationCounter, List<AttributeValueWithClassificationCounter>> getSortedListOfAttributeValuesWithClassificationCounters(
            final Iterable<? extends AbstractInstance> instances, final String attribute, String splitAttribute, Serializable splitAttributeValue, final Serializable minorityClassification) {

        Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> totalsClassificationCounterPairedWithMapofClassificationCounters = countAllByAttributeValues(instances,attribute,splitAttribute,splitAttributeValue);
        final Map<Serializable, ClassificationCounter> result = totalsClassificationCounterPairedWithMapofClassificationCounters.getValue1();
		final ClassificationCounter totals = totalsClassificationCounterPairedWithMapofClassificationCounters.getValue0();

        List<AttributeValueWithClassificationCounter> attributesWithClassificationCounters = Lists.newArrayList();
        for(Serializable key : result.keySet()) {
            attributesWithClassificationCounters.add(new AttributeValueWithClassificationCounter(key, result.get(key)));
        }
        Collections.sort(attributesWithClassificationCounters, new Comparator<AttributeValueWithClassificationCounter>() {
            @Override
            public int compare(AttributeValueWithClassificationCounter cc1, AttributeValueWithClassificationCounter cc2) {
                double p1 = cc1.classificationCounter.getCount(minorityClassification) / cc1.classificationCounter.getTotal();
                double p2 = cc2.classificationCounter.getCount(minorityClassification) / cc2.classificationCounter.getTotal();

                if (p2 > p1)
                    return 1;
                else if (p1 == p2)
                    return 0;
                else
                    return -1;
            }
        });

		return Pair.with(totals, attributesWithClassificationCounters);
	}

    private static boolean isAnAcceptableMissingValue(AbstractInstance instance, String splitAttribute, Serializable splitAttributeValue){
        return  splitAttribute == null
                || splitAttributeValue == null
                || instance.getAttributes().get(splitAttribute).equals(splitAttributeValue);
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
