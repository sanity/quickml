package quickml.supervised.classifier.tree.decisionTree.tree;


import quickml.data.AttributesMap;
import quickml.supervised.classifier.tree.Leaf;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatsAndOperations;

import java.io.Serializable;
import java.util.Set;

public abstract class Node<TS extends TermStatsAndOperations<TS>> implements Serializable {
    private static final long serialVersionUID = -8713974861744567620L;

    public abstract void dump(int indent, Appendable ap);

    public final Node parent;

    public Node(Node parent) {
        this.parent = parent;
    }

    public abstract double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attribute);

	/**
	 * Writes a textual representation of this tree to a PrintStream
	 * 
	 * @param ap
	 */
	public void dump(final Appendable ap) {
		dump(0, ap);
	}

	/**
	 * Get a label for a given set of HashMapAttributes
	 * 
	 * @param attributes
	 * @return
	 */
	public abstract Leaf<TS> getLeaf(AttributesMap attributes);

	/**
	 * Return the mean depth of leaves in the tree. A lower number generally
	 * indicates that the decision tree learner has done a better job.
	 * 
	 * @return
	 */
	public double meanDepth() {
		final LeafDepthStats stats = new LeafDepthStats();
		calcMeanDepth(stats);
		return (double) stats.ttlDepth / stats.ttlSamples;
	}

	/**
	 * Return the number of nodes in this decision tree.
	 * 
	 * @return
	 */
	public abstract int size();

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public abstract int hashCode();

    public abstract void calcMeanDepth(LeafDepthStats stats);

	protected static class LeafDepthStats {
		int ttlDepth = 0;
		int ttlSamples = 0;
	}
}
