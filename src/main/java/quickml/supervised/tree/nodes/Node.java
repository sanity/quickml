package quickml.supervised.tree.nodes;


import quickml.data.AttributesMap;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;


import java.io.Serializable;
import java.util.Set;

public interface Node<TS extends TermStatsAndOperations<TS>> {

	public abstract Leaf<TS> getLeaf(AttributesMap attributes);

	public abstract Node<TS> getParent();

	@Override
	public abstract boolean equals(final Object obj);

	@Override
	public abstract int hashCode();
	//last 2 are optional

	public abstract void calcMeanDepth(LeafDepthStats stats);

	/**
	 * Return the number of nodes in this decision tree.
	 *
	 * @return
	 */
	public abstract int getSize();

}
