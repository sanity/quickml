package quickdt.predictiveModels.decisionTree.tree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class Leaf extends Node {
	private static final long serialVersionUID = -5617660873196498754L;

    private static final AtomicLong guidCounter = new AtomicLong(0);

    public final long guid;

	/**
	 * How deep in the tree is this label? A lower number typically indicates a
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
    public final ClassificationCounter classificationCounts;

    protected transient volatile Map.Entry<Serializable, Double> bestClassificationEntry = null;

    public Leaf(Node parent, final Iterable<? extends AbstractInstance> instances, final int depth) {
        this(parent, ClassificationCounter.countAll(instances), depth);
        Preconditions.checkArgument(!Iterables.isEmpty(instances), "Can't create leaf with no instances");
	}

    public Leaf(Node parent, final ClassificationCounter classificationCounts, final int depth) {
        super(parent);
        guid = guidCounter.incrementAndGet();
        this.classificationCounts = classificationCounts;
        Preconditions.checkState(classificationCounts.getTotal() > 0, "Classifications must be > 0");
        exampleCount = classificationCounts.getTotal();
        this.depth = depth;
    }

    /**
     *
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
	public void dump(final int indent, final PrintStream ps) {
		for (int x = 0; x < indent; x++) {
			ps.print(' ');
		}
		ps.println(this);
	}

	@Override
	public Leaf getLeaf(final Attributes attributes) {
		return this;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	protected void calcMeanDepth(final LeafDepthStats stats) {
		stats.ttlDepth += depth * exampleCount;
		stats.ttlSamples += exampleCount;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Serializable key : getClassifications()) {
            builder.append(key+"="+this.getProbability(key)+" ");
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

    public Set<Serializable> getClassifications() {
        return classificationCounts.getCounts().keySet();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Leaf leaf = (Leaf) o;

        if (depth != leaf.depth) return false;
        if (Double.compare(leaf.exampleCount, exampleCount) != 0) return false;
        if (!classificationCounts.equals(leaf.classificationCounts)) return false;

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
