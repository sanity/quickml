package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree;


import com.google.common.collect.Maps;
import quickml.data.AttributesMap;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;

public abstract class OldNode implements Serializable {
    private static final long serialVersionUID = -8713974861744567620L;

    public abstract void dump(int indent, Appendable ap);

    public final OldNode parent;

    public OldNode(OldNode parent) {
        this.parent = parent;
    }

    public abstract double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attribute);

	/**
	 * Writes a textual representation of this oldTree to a PrintStream
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
	public abstract OldLeaf getLeaf(AttributesMap attributes);

	/**
	 * Return the mean depth of leaves in the oldTree. A lower number generally
	 * indicates that the decision oldTree learner has done a better job.
	 * 
	 * @return
	 */
	public double meanDepth() {
		final LeafDepthStats stats = new LeafDepthStats();
		calcLeafDepthStats(stats);
		return (double) stats.ttlDepth / stats.ttlSamples;
	}

	public double medianDepth() {
		LeafDepthStats leafDepthStats = new LeafDepthStats();
		calcLeafDepthStats(leafDepthStats);
		long counts = 0;
		int depth = 0;
		while (counts < leafDepthStats.ttlSamples/2) {
			if (leafDepthStats.depthDistribution.containsKey(depth)) {
				counts += leafDepthStats.depthDistribution.get(depth);
			}
			if (counts < leafDepthStats.ttlSamples/2) {
				depth++;
			}

		}
		return depth;
	}
/*
	public double medianDepth() {
		final LeafDepthStats stats = new LeafDepthStats();
		calcLeafDepthStats(stats);
		Collections.sort(stats.sampleDepths);
		return stats.sampleDepths.get(stats.sampleDepths.size()/2);
	}
*/
	/**
	 * Return the number of nodes in this decision oldTree.
	 * 
	 * @return
	 */
	public abstract int size();

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public abstract int hashCode();

    protected abstract void calcLeafDepthStats(LeafDepthStats stats);

	protected static class LeafDepthStats {
		int ttlDepth = 0;
		int ttlSamples = 0;
		public TreeMap<Integer, Long> depthDistribution = Maps.newTreeMap();
	}
}
