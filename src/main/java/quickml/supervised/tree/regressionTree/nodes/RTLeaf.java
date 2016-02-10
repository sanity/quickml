package quickml.supervised.tree.regressionTree.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import quickml.data.AttributesMap;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.LeafDepthStats;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


public class RTLeaf implements Leaf<MeanValueCounter>, Serializable {
    private static final long serialVersionUID = -5617660873196498754L;

    private static final AtomicLong guidCounter = new AtomicLong(0);

    public final long guid;

    public MeanValueCounter getValueCounter() {
        return meanValueCounter;
    }

    /**
     * How deep in the oldTree is this label? A lower number typically indicates a
     * more confident getBestClassification.
     */
    public final int depth;

    public final Branch<MeanValueCounter> parent;
    /**
     * How many training examples matched this leaf? A higher number indicates a
     * more confident getBestClassification.
     */
    public double exampleCount;
    /**
     * The actual getBestClassification counts
     */
    private final MeanValueCounter meanValueCounter;


    public RTLeaf(Branch<MeanValueCounter> parent, final Iterable<? extends RegressionInstance> instances, final int depth) {
        this(parent, MeanValueCounter.accumulateAll(instances), depth);
        Preconditions.checkArgument(!Iterables.isEmpty(instances), "Can't create leaf with no instances");
    }

    public RTLeaf(Branch<MeanValueCounter> parent, final MeanValueCounter meanValueCounter, final int depth) {
        guid = guidCounter.incrementAndGet();
        this.meanValueCounter = meanValueCounter;
        Preconditions.checkState(meanValueCounter.getTotal() > 0, "Classifications must be > 0");
        exampleCount = meanValueCounter.getTotal();
        this.depth = depth;
        this.parent = parent;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public Branch<MeanValueCounter> getParent() {
        return parent;
    }


    /**
     * @return The most likely classification
     */

    @Override
    public RTLeaf getLeaf(final AttributesMap attributes) {
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

        builder.append("average=  " + this.meanValueCounter.toString());

        return builder.toString();
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RTLeaf RTLeaf = (RTLeaf) o;

        if (depth != RTLeaf.depth) return false;
        if (Double.compare(RTLeaf.exampleCount, exampleCount) != 0) return false;
        if (!meanValueCounter.equals(RTLeaf.meanValueCounter)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = depth;
        temp = Double.doubleToLongBits(exampleCount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + meanValueCounter.hashCode();
        return result;
    }
}
