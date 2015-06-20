package quickml.supervised.tree.nodes;


import quickml.supervised.tree.summaryStatistics.ValueCounter;

public interface NodeBase<VC extends ValueCounter<VC>> {

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
