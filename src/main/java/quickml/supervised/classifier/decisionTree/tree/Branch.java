package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Predicate;
import quickml.data.Instance;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


public abstract class Branch extends Node {
	private static final long serialVersionUID = 8290012786245422175L;

	public final String attribute;

	public Node trueChild, falseChild;

	public Branch(Node parent, final String attribute) {
		super(parent);
        this.attribute = attribute;
	}

	public abstract boolean decide(Map<String, Serializable> attributes);

	@Override
	public int size() {
		return 1 + trueChild.size() + falseChild.size();
	}

	public Predicate<Instance<Map<String, Serializable>>> getInPredicate() {
		return new Predicate<Instance<Map<String, Serializable>>>() {

			@Override
			public boolean apply(final Instance<Map<String, Serializable>> input) {
				return decide(input.getAttributes());
			}
		};
	}

	public Predicate<Instance<Map<String, Serializable>>> getOutPredicate() {
		return new Predicate<Instance<Map<String, Serializable>>>() {

			@Override
			public boolean apply(final Instance<Map<String, Serializable>> input) {
				return !decide(input.getAttributes());
			}
		};
	}

	@Override
	public Leaf getLeaf(final Map<String, Serializable> attributes) {
		if (decide(attributes))
			return trueChild.getLeaf(attributes);
		else
			return falseChild.getLeaf(attributes);
	}

	@Override
	public void dump(final int indent, final Appendable ap) {
        try {
            for (int x = 0; x < indent; x++) {
                ap.append(' ');
            }
            ap.append(this+"\n");
            trueChild.dump(indent + 2, ap);
            for (int x = 0; x < indent; x++) {
                ap.append(' ');
            }
            ap.append(toNotString() +"\n");
            falseChild.dump(indent + 2, ap);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }

	}

	public abstract String toNotString();

	@Override
	protected void calcMeanDepth(final LeafDepthStats stats) {
		trueChild.calcMeanDepth(stats);
		falseChild.calcMeanDepth(stats);
	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Branch branch = (Branch) o;

        if (!attribute.equals(branch.attribute)) return false;
        if (!falseChild.equals(branch.falseChild)) return false;
        if (!trueChild.equals(branch.trueChild)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        result = 31 * result + trueChild.hashCode();
        result = 31 * result + falseChild.hashCode();
        return result;
    }
}

