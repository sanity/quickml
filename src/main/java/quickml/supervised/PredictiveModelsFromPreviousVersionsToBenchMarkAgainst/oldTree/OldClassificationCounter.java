package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.javatuples.Pair;
import quickml.collections.ValueSummingMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import static quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTreeBuilder.MISSING_VALUE;


public class OldClassificationCounter implements Serializable {
    private static final long serialVersionUID = -6821237234748044623L;
    private final ValueSummingMap<Serializable> counts = new ValueSummingMap<Serializable>();

    public OldClassificationCounter() {
    }

    public OldClassificationCounter(OldClassificationCounter classificationCounter) {
        this.counts.putAll(classificationCounter.counts);
    }

    public OldClassificationCounter(ClassificationCounter classificationCounter) {
        this.counts.putAll(classificationCounter.getCounts());
    }

    private boolean hasSufficientData = true;

    public void setHasSufficientData(boolean hasSufficientData) {
        this.hasSufficientData = hasSufficientData;
    }

    public boolean hasSufficientData() {
        return hasSufficientData;
    }

    public static OldClassificationCounter merge(OldClassificationCounter a, OldClassificationCounter b) {
        OldClassificationCounter newCC = new OldClassificationCounter();
        newCC.counts.putAll(a.counts);
        for (Entry<Serializable, Number> e : b.counts.entrySet()) {
            newCC.counts.addToValue(e.getKey(), e.getValue().doubleValue());
        }
        return newCC;
    }

    public static Pair<OldClassificationCounter, Map<Serializable, OldClassificationCounter>> countAllByAttributeValues(
            final Iterable<? extends ClassifierInstance> instances, final String attribute) {
        final Map<Serializable, OldClassificationCounter> result = Maps.newHashMap();
        final OldClassificationCounter totals = new OldClassificationCounter();
        for (ClassifierInstance instance : instances) {
            final Serializable attrVal = instance.getAttributes().get(attribute);
            OldClassificationCounter cc;
            boolean acceptableMissingValue = attrVal == null;

            if (attrVal != null)
                cc = result.get(attrVal);
            else if (acceptableMissingValue)
                cc = result.get(MISSING_VALUE);
            else
                continue;

            if (cc == null) {
                cc = new OldClassificationCounter();
                Serializable newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
                result.put(newKey, cc);
            }
            cc.addClassification(instance.getLabel(), instance.getWeight());
            totals.addClassification(instance.getLabel(), instance.getWeight());
        }

        return Pair.with(totals, result);
    }

    public static Pair<OldClassificationCounter, List<OldAttributeValueWithClassificationCounter>> getSortedListOfAttributeValuesWithClassificationCounters(
            final Iterable<? extends ClassifierInstance> instances, final String attribute, final Serializable minorityClassification) {

        Pair<OldClassificationCounter, Map<Serializable, OldClassificationCounter>> totalsClassificationCounterPairedWithMapofClassificationCounters = countAllByAttributeValues(instances, attribute);
        final Map<Serializable, OldClassificationCounter> result = totalsClassificationCounterPairedWithMapofClassificationCounters.getValue1();
        final OldClassificationCounter totals = totalsClassificationCounterPairedWithMapofClassificationCounters.getValue0();

        List<OldAttributeValueWithClassificationCounter> attributesWithClassificationCounters = Lists.newArrayList();
        for (Serializable key : result.keySet()) {
            attributesWithClassificationCounters.add(new OldAttributeValueWithClassificationCounter(key, result.get(key)));
        }

        Collections.sort(attributesWithClassificationCounters, new Comparator<OldAttributeValueWithClassificationCounter>() {
            @Override
            public int compare(OldAttributeValueWithClassificationCounter cc1, OldAttributeValueWithClassificationCounter cc2) {
                double probOfMinority1 = cc1.classificationCounter.getCount(minorityClassification) / cc1.classificationCounter.getTotal();
                double probOfMinority2 = cc2.classificationCounter.getCount(minorityClassification) / cc2.classificationCounter.getTotal();

                return Ordering.natural().reverse().compare(probOfMinority1, probOfMinority2);
            }
        });

        return Pair.with(totals, attributesWithClassificationCounters);
    }


    public Map<Serializable, Double> getCounts() {
        Map<Serializable, Double> ret = Maps.newHashMap();
        for (Entry<Serializable, Number> serializableNumberEntry : counts.entrySet()) {
            ret.put(serializableNumberEntry.getKey(), serializableNumberEntry.getValue().doubleValue());
        }
        return ret;
    }


    public static OldClassificationCounter countAll(final Iterable<? extends ClassifierInstance> instances) {
        final OldClassificationCounter result = new OldClassificationCounter();
        for (ClassifierInstance instance : instances) {
            result.addClassification(instance.getLabel(), instance.getWeight());
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

    public OldClassificationCounter add(final OldClassificationCounter other) {
        final OldClassificationCounter result = new OldClassificationCounter();
        result.counts.putAll(counts);
        for (final Entry<Serializable, Number> e : other.counts.entrySet()) {
            result.counts.addToValue(e.getKey(), e.getValue().doubleValue());
        }
        return result;
    }

    public OldClassificationCounter subtract(final OldClassificationCounter other) {
        final OldClassificationCounter result = new OldClassificationCounter();
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

        OldClassificationCounter that = (OldClassificationCounter) o;

        if (!counts.equals(that.counts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return counts.hashCode();
    }
}
