package com.moboscope.quickdt;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

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
		final Pair<Integer, Map<Serializable, Integer>> outcomeCounts = TreeBuilder.calcOutcomeCounts(instances);
		if (outcomeCounts.getValue1().size() == 1) {
			classification = outcomeCounts.getValue1().keySet().iterator().next();
			this.depth = depth;
			exampleCount = outcomeCounts.getValue0();
			probability = 1;
		} else {
			// Determine best label
			Entry<Serializable, Integer> best = null;

			for (final Entry<Serializable, Integer> e : outcomeCounts.getValue1().entrySet()) {
				if (best == null || e.getValue() > best.getValue()) {
					best = e;
				}
			}

			classification = best.getKey();
			this.depth = depth;
			exampleCount = outcomeCounts.getValue0();
			probability = (double) best.getValue()
					/ (double) outcomeCounts.getValue0();
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
