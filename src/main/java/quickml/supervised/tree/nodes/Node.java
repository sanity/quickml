package quickml.supervised.tree.nodes;


import quickml.data.AttributesMap;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
«

import java.io.Serializable;
import java.util.Set;

public abstract class Node<TS extends TermStatsAndOperations<TS>> implements Serializable {
    private static final long serialVersionUID = -8713974861744567620L;


    public final Node<TS> parent;

    public Node(Node<TS> parent) {
        this.parent = parent;
    }

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
