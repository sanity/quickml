package com.moboscope.quickdt;

import java.io.PrintStream;

import com.google.common.base.Predicate;


public abstract class Branch extends Node {
	private static final long serialVersionUID = 8290012786245422175L;

	public Node trueChild, falseChild;

	protected abstract boolean decide(Attributes attributes);

	@Override
	public int size() {
		return 1 + trueChild.size() + falseChild.size();
	}

	@Override
	public boolean fullRecall() {
		return trueChild.fullRecall() && falseChild.fullRecall();
	}

	public Predicate<Instance> getInPredicate() {
		return new Predicate<Instance>() {

			@Override
			public boolean apply(final Instance input) {
				return decide(input.attributes);
			}
		};
	}

	public Predicate<Instance> getOutPredicate() {
		return new Predicate<Instance>() {

			@Override
			public boolean apply(final Instance input) {
				return !decide(input.attributes);
			}
		};
	}

	@Override
	public Leaf getLeaf(final Attributes attributes) {
		if (decide(attributes))
			return trueChild.getLeaf(attributes);
		else
			return falseChild.getLeaf(attributes);
	}

	@Override
	public void dump(final int indent, final PrintStream ps) {
		for (int x = 0; x < indent; x++) {
			ps.print(' ');
		}
		ps.println(this);
		trueChild.dump(indent + 2, ps);
		for (int x = 0; x < indent; x++) {
			ps.print(' ');
		}
		ps.println(toNotString());
		falseChild.dump(indent + 2, ps);
	}

	public abstract String toNotString();

	@Override
	protected void calcMeanDepth(final LeafDepthStats stats) {
		trueChild.calcMeanDepth(stats);
		falseChild.calcMeanDepth(stats);
	}
}

