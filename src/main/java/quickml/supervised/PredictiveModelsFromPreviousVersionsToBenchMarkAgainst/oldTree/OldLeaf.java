package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class OldLeaf extends OldNode {
    private static final long serialVersionUID = -5617660873196498754L;

    private static final AtomicLong guidCounter = new AtomicLong(0);

    public final long guid;

    /**
     * How deep in the oldTree is this label? A lower number typically indicates a
     * more confident getBestClassification.
     */
    public final int depth;
    /**
     * How many training examples matched this leaf? A higher number indicates a
     * more confident getBestClassification.
     */
    public double exampleCount;
    /**
     * The actual getBestClassification counts
     */
    public final OldClassificationCounter classificationCounts;

    protected transient volatile Map.Entry<Serializable, Double> bestClassificationEntry = null;


    public OldLeaf(OldNode parent, final Iterable<? extends ClassifierInstance> instances, final int depth) {
        this(parent, OldClassificationCounter.countAll(instances), depth);
        Preconditions.checkArgument(!Iterables.isEmpty(instances), "Can't create leaf with no instances");
    }

    public OldLeaf(OldNode parent, final OldClassificationCounter classificationCounts, final int depth) {
        super(parent);
        guid = guidCounter.incrementAndGet();
        this.classificationCounts = classificationCounts;
        Preconditions.checkState(classificationCounts.getTotal() > 0, "Classifications must be > 0");
        exampleCount = classificationCounts.getTotal();
        this.depth = depth;
    }

    /**
     * @return The most likely classification
     */

    public Serializable getBestClassification() {
        return getBestClassificationEntry().getKey();
    }

    protected synchronized Map.Entry<Serializable, Double> getBestClassificationEntry() {
        if (bestClassificationEntry != null) return bestClassificationEntry;

        for (Map.Entry<Serializable, Double> e : classificationCounts.getCounts().entrySet()) {
            if (bestClassificationEntry == null || e.getValue() > bestClassificationEntry.getValue()) {
                bestClassificationEntry = e;
            }
        }

        return bestClassificationEntry;
    }

    @Override
    public void dump(final int indent, final Appendable ap) {
        try {
            for (int x = 0; x < indent; x++) {
                ap.append(' ');
            }
            ap.append(this + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public OldLeaf getLeaf(final AttributesMap attributes) {
        return this;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected void calcLeafDepthStats(final LeafDepthStats stats) {
        stats.ttlDepth += depth * exampleCount;
        stats.ttlSamples += exampleCount;
        Map<Integer, Long> dist = stats.depthDistribution;
        if (dist.containsKey(depth)) {
            dist.put(depth, dist.get(depth) + (long) exampleCount);
        } else {
            dist.put(depth, (long) exampleCount);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Serializable key : getClassifications()) {
            builder.append(key + "=" + this.getProbability(key) + " ");
        }
        return builder.toString();
    }

    public double getProbability(Serializable classification) {
        final double totalCount = classificationCounts.getTotal();
        if (totalCount == 0) {
            throw new IllegalStateException("Trying to get a probability from a Leaf with no examples");
        }
        final double probability = classificationCounts.getCount(classification) / totalCount;
        return probability;
    }

    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attribute) {
        return getProbability(classification);
    }

    public Set<Serializable> getClassifications() {
        return classificationCounts.getCounts().keySet();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OldLeaf oldLeaf = (OldLeaf) o;

        if (depth != oldLeaf.depth) return false;
        if (Double.compare(oldLeaf.exampleCount, exampleCount) != 0) return false;
        if (!classificationCounts.equals(oldLeaf.classificationCounts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = depth;
        temp = Double.doubleToLongBits(exampleCount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + classificationCounts.hashCode();
        return result;
    }
}
