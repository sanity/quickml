package com.moboscope.quickdt;

import java.io.*;

import org.javatuples.Pair;

public class Leaf extends Node {
	private static final long serialVersionUID = -5617660873196498754L;

	/**
	 * How deep in the tree is this label? A lower number typically indicates a
	 * more confident classification.
	 */
	public int depth;

	/**
	 * How many training examples matched this leaf? A higher number indicates a
	 * more confident classification.
	 */
	public final int exampleCount;

	/**
	 * What label was assigned by this leaf?
	 */
	public Serializable classification;

	/**
	 * What is the probability that this classification is correct based on the
	 * training data?
	 */
	public double probability;

	public Leaf(final Iterable<Instance> instances, final int depth) {
		final ClassificationCounter classificationCounts = ClassificationCounter.countAll(instances);
		if (classificationCounts.getTotal() == 1) {
			classification = classificationCounts.allClassifications().iterator().next();
			this.depth = depth;
			exampleCount = classificationCounts.getTotal();
			probability = 1;
		} else {
			// Determine best label
			final Pair<Serializable, Integer> best = classificationCounts.mostPopular();

			classification = best.getValue0();
			this.depth = depth;
			exampleCount = classificationCounts.getTotal();
			probability = (double) best.getValue1()
					/ (double) classificationCounts.getTotal();
		}
	}


	@Override
	public void dump(final int indent, final PrintStream ps) {
		for (int x = 0; x < indent; x++) {
			ps.print(' ');
		}
		ps.println(this);
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
	public boolean fullRecall() {
		return probability == 1.0;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[classification=");
		builder.append(classification);
		builder.append(", probability=");
		builder.append(probability);
		builder.append(", depth=");
		builder.append(depth);
		builder.append(", exampleCount=");
		builder.append(exampleCount);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public Leaf getLeaf(final Attributes attributes) {
		return this;
	}

}
