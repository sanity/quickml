package quickml.supervised.tree.decisionTree.valueCounters;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import quickml.collections.ValueSummingMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;


public class ClassificationCounter extends ValueCounter<ClassificationCounter> implements Serializable {
    private static final long serialVersionUID = -6821237234748044623L;
    private final ValueSummingMap<Serializable> counts = new ValueSummingMap<Serializable>();
    //TODO: remove hasSuffientData stuff after debugging
    private boolean hasSufficientData = true;
    public void setHasSufficientData(boolean hasSufficientData) {
        this.hasSufficientData = hasSufficientData;
    }

    public boolean hasSufficientData() {
        return hasSufficientData;
    }
    public ClassificationCounter() {}

    public ClassificationCounter(Serializable attrVal) {
        super(attrVal);
    }
    public boolean isEmpty() {
        return counts.isEmpty();
    }

    public ClassificationCounter(ClassificationCounter classificationCounter) {
        super(classificationCounter.attrVal);
        this.counts.putAll(classificationCounter.counts);
    }
    public ClassificationCounter(HashMap<Serializable, ? extends Number> mapOfCounts) {
        for (Serializable classification: mapOfCounts.keySet()) {
            counts.addToValue(classification, mapOfCounts.get(classification).doubleValue());
        }
    }

    public static ClassificationCounter merge(ClassificationCounter a, ClassificationCounter b) {
        ClassificationCounter newCC = new ClassificationCounter();
        newCC.counts.putAll(a.counts);
        for (Entry<Serializable, Number> e : b.counts.entrySet()) {
            newCC.counts.addToValue(e.getKey(), e.getValue().doubleValue());
        }
        return newCC;
    }

    public static Serializable getLeastPopularClass(ClassificationCounter classificationCounter) {
        Serializable minClass = null;
        double minCounts = Double.MAX_VALUE;
        for (Serializable classification : classificationCounter.allClassifications()) {
            if (classificationCounter.getCount(classification) < minCounts) {
                minCounts = classificationCounter.getCount(classification);
                minClass = classification;
            }
        }
        return minClass;
    }

    public static Serializable getMostPopularClass(ClassificationCounter classificationCounter) {
        Serializable maxClass = null;
        double maxCounts = 0;
        Serializable leastPopular = getLeastPopularClass(classificationCounter); //want to ensure don't have the same leastPopular as mostPopular when class ballance is 50/50
        for (Serializable classification : classificationCounter.allClassifications()) {
            if (classificationCounter.getCount(classification) > maxCounts || !classification.equals(leastPopular)) {
                maxCounts = classificationCounter.getCount(classification);
                maxClass = classification;
            }
        }
        return maxClass;
    }


    //should be abstracted.  Data should be in an inner class


    public Map<Serializable, Double> getCounts() {
        Map<Serializable, Double> ret = Maps.newHashMap();
        for (Entry<Serializable, Number> serializableNumberEntry : counts.entrySet()) {
            ret.put(serializableNumberEntry.getKey(), serializableNumberEntry.getValue().doubleValue());
        }
        return ret;
    }


    public static ClassificationCounter countAll(final Iterable<? extends ClassifierInstance> instances) {
        final ClassificationCounter result = new ClassificationCounter();
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

    @Override
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

    @Override
    public String toString() {
        return getCounts().toString();
    }
}
