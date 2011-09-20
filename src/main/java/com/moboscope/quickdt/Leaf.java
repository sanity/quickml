package com.moboscope.quickdt;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.javatuples.Pair;


public class Leaf extends Node {
	private static final long serialVersionUID = -5617660873196498754L;

	public Leaf(final Iterable<Instance> instances, final int depth) {
		final Pair<Integer, Map<Serializable, Integer>> outcomeCounts = TreeBuilder.calcOutcomeCounts(instances);
		if (outcomeCounts.getValue1().size() == 1) {
			label = new Label(outcomeCounts.getValue1().keySet().iterator().next(), depth, outcomeCounts.getValue0(), 1);
		} else {
			// Determine best label
			Entry<Serializable, Integer> best = null;

			for (final Entry<Serializable, Integer> e : outcomeCounts.getValue1().entrySet()) {
				if (best == null || e.getValue() > best.getValue()) {
					best = e;
				}
			}

			label = new Label(best.getKey(), depth, outcomeCounts.getValue0(), (double) best.getValue()
					/ (double) outcomeCounts.getValue0());
		}
	}

	public final Label label;

	@Override
	public Label getLabel(final Attributes attributes) {
		return label;
	}

	@Override
	public void dump(final int indent, final PrintStream ps) {
		for (int x = 0; x < indent; x++) {
			ps.print(' ');
		}
		ps.println(label);
	}

	@Override
	public int size() {
		return 1;
	}

}
