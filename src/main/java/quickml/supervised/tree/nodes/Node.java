package quickml.supervised.tree.nodes;


import quickml.data.AttributesMap;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;


import java.io.Serializable;
import java.util.Set;

public interface Node<TS extends TermStatsAndOperations<TS>> {
	//private static final long serialVersionUID = -8713974861744567620L;


	public abstract Leaf<TS> getLeaf(AttributesMap attributes);

	public abstract Node<TS> getParent();
	/**
	 * Return the mean depth of leaves in the tree. A lower number generally
	 * indicates that the decision tree learner has done a better job.
	 *
	 * @return
	 */

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

}
