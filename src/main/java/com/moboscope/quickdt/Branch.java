package com.moboscope.quickdt;

import java.io.PrintStream;

import com.google.common.base.Predicate;


public abstract class Branch extends Node {
	public Node trueChild, falseChild;

	protected abstract boolean decide(Attributes attributes);

	@Override
	public int size() {
		return 1 + trueChild.size() + falseChild.size();
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
	public Label getLabel(final Attributes attributes) {
		final Label cl;
		if (decide(attributes)) {
			cl = trueChild.getLabel(attributes);
		} else {
			cl = falseChild.getLabel(attributes);
		}

		return new Label(cl.output, cl.depth + 1, cl.exampleCount, cl.probability);
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
}

