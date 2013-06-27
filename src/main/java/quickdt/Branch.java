package quickdt;

import com.google.common.base.Predicate;

import java.io.PrintStream;


public abstract class Branch extends Node {
	private static final long serialVersionUID = 8290012786245422175L;

	public final String attribute;

	public Node trueChild, falseChild;

	public Branch(Node parent, final String attribute) {
		super(parent);
        this.attribute = attribute;
	}

	protected abstract boolean decide(Attributes attributes);

	@Override
	public int size() {
		return 1 + trueChild.size() + falseChild.size();
	}

	@Override
	public boolean fullRecall() {
		return trueChild.fullRecall() && falseChild.fullRecall();
	}

	public Predicate<AbstractInstance> getInPredicate() {
		return new Predicate<AbstractInstance>() {

			@Override
			public boolean apply(final AbstractInstance input) {
				return decide(input.getAttributes());
			}
		};
	}

	public Predicate<AbstractInstance> getOutPredicate() {
		return new Predicate<AbstractInstance>() {

			@Override
			public boolean apply(final AbstractInstance input) {
				return !decide(input.getAttributes());
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

