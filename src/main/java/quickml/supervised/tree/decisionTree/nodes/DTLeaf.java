package quickml.supervised.tree.decisionTree.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.LeafDepthStats;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;



public class DTLeaf implements Leaf<ClassificationCounter>, Serializable {
    private static final long serialVersionUID = -5617660873196498754L;

    private static final AtomicLong guidCounter = new AtomicLong(0);

    public final long guid;

    public ClassificationCounter getValueCounter() {
        return classificationCounts;
    }

    /**
     * How deep in the oldTree is this label? A lower number typically indicates a
     * more confident getBestClassification.
     */
    public final int depth;

    public final Branch<ClassificationCounter> parent;
    /**
     * How many training examples matched this leaf? A higher number indicates a
     * more confident getBestClassification.
     */
    public double exampleCount;
    /**
     * The actual getBestClassification counts
     */
    private final ClassificationCounter classificationCounts;


    public DTLeaf(Branch<ClassificationCounter> parent, final Iterable<? extends ClassifierInstance> instances, final int depth) {
        this(parent, ClassificationCounter.countAll(instances), depth);
        Preconditions.checkArgument(!Iterables.isEmpty(instances), "Can't create leaf with no instances");
    }

    public DTLeaf(Branch<ClassificationCounter> parent, final ClassificationCounter classificationCounts, final int depth) {
        guid = guidCounter.incrementAndGet();
        this.classificationCounts = classificationCounts;
        Preconditions.checkState(classificationCounts.getTotal() > 0, "Classifications must be > 0");
        exampleCount = classificationCounts.getTotal();
        this.depth = depth;
        this.parent = parent;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public Branch<ClassificationCounter> getParent() {
        return parent;
    }


    /**
     * @return The most likely classification
     */

    @Override
    public DTLeaf getLeaf(final AttributesMap attributes) {
        return this;
    }

    @Override
    public int getSize() {
        return 1;
    }

    //TODO: move this up when Java 8 is migrated too
    @Override
    public void calcLeafDepthStats(final LeafDepthStats stats) {
        stats.ttlDepth += depth * exampleCount;
        stats.ttlSamples += exampleCount;
        Map<Integer, Long> dist = stats.depthDistribution;
        if (dist.containsKey(depth)) {
            dist.put(depth, dist.get(depth) + (long)exampleCount);
        } else {
            dist.put(depth, (long)exampleCount);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Serializable key : classificationCounts.getCounts().keySet()) {
            builder.append(key + "=" + this.classificationCounts.getCounts().get(key)/classificationCounts.getTotal() + " ");
        }
        return builder.toString();
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DTLeaf DTLeaf = (DTLeaf) o;

        if (depth != DTLeaf.depth) return false;
        if (Double.compare(DTLeaf.exampleCount, exampleCount) != 0) return false;
        if (!classificationCounts.equals(DTLeaf.classificationCounts)) return false;

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
