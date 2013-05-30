package quickdt;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Leaf extends Node {
	private static final long serialVersionUID = -5617660873196498754L;
	/**
	 * How deep in the tree is this label? A lower number typically indicates a
	 * more confident getBestClassification.
	 */
	public final int depth;
	/**
	 * How many training examples matched this leaf? A higher number indicates a
	 * more confident getBestClassification.
	 */
	public final int exampleCount;
    /**
     * The actual getBestClassification counts
     */
    public final ClassificationCounter classificationCounts;

    protected transient volatile Map.Entry<Serializable, Integer> bestClassificationEntry = null;

    public Leaf(Node parent, final Iterable<Instance> instances, final int depth) {
		super(parent);
        classificationCounts = ClassificationCounter.countAll(instances);
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

    protected synchronized Map.Entry<Serializable, Integer> getBestClassificationEntry() {
        if (bestClassificationEntry != null) return bestClassificationEntry;

        for (Map.Entry<Serializable, Integer> e : classificationCounts.getCounts().entrySet()) {
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
    public boolean fullRecall() {
        return getBestClassificationProbability() == 1.0;
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

    public double getBestClassificationProbability() {
        return (double) getBestClassificationEntry().getValue() / (double) this.exampleCount;
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
        return (double) classificationCounts.getCount(classification) / (double) exampleCount;
    }

    public Set<Serializable> getClassifications() {
        return classificationCounts.getCounts().keySet();
    }
}
