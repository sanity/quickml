package quickml.supervised.tree.nodes;


import quickml.data.AttributesMap;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;

public interface Node<VC extends ValueCounter<VC>> {

	public abstract Leaf<VC> getLeaf(AttributesMap attributes);

	public abstract Node<VC> getParent();

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
