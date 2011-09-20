package com.moboscope.quickdt;

import java.io.*;

public abstract class Node implements Serializable {
	public abstract void dump(int indent, PrintStream ps);

	/**
	 * Writes a textual representation of this tree to a PrintStream
	 * 
	 * @param ps
	 */
	public void dump(final PrintStream ps) {
		dump(0, ps);
	}

	/**
	 * Get a label for a given set of Attributes
	 * 
	 * @param attributes
	 * @return
	 */
	public abstract Label getLabel(Attributes attributes);

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

	protected abstract void calcMeanDepth(LeafDepthStats stats);

	public static class Label implements Serializable {
		private static final long serialVersionUID = -4063175796311033721L;

		/**
		 * How deep in the tree is this label? A lower number typically
		 * indicates a more confident classification.
		 */
		public int depth;

		/**
		 * How many training examples matched this leaf? A higher number
		 * indicates a more confident classification.
		 */
		public final int exampleCount;

		/**
		 * What label was assigned by this leaf?
		 */
		public Serializable output;

		/**
		 * What is the probability that this classification is correct based on
		 * the training data?
		 */
		public double probability;

		public Label(final Serializable output, final int depth, final int exampleCount, final double probability) {
			this.output = output;
			this.depth = depth;
			this.exampleCount = exampleCount;
			this.probability = probability;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("[output=");
			builder.append(output);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", exampleCount=");
			builder.append(exampleCount);
			builder.append(", probability=");
			builder.append(probability);
			builder.append("]");
			return builder.toString();
		}

	}

	protected static class LeafDepthStats {
		int ttlDepth = 0;
		int ttlSamples = 0;
	}
}
