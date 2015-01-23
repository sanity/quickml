package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Predicate;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;


public abstract class Branch extends Node {
	private static final long serialVersionUID = 8290012786245422175L;

	public final String attribute;

	public Node trueChild, falseChild;
    private double probabilityOfTrueChild;

	public Branch(Node parent, final String attribute, double probabilityOfTrueChild) {
		super(parent);
        this.probabilityOfTrueChild = probabilityOfTrueChild;
        this.attribute = attribute;
	}

	public abstract boolean decide(AttributesMap attributes);

	@Override
	public int size() {
		return 1 + trueChild.size() + falseChild.size();
	}

	public Predicate<Instance<AttributesMap, Serializable>> getInPredicate() {
		return new Predicate<Instance<AttributesMap, Serializable>>() {

			@Override
			public boolean apply(final Instance<AttributesMap, Serializable> input) {
				return decide(input.getAttributes());
			}
		};
	}

	public Predicate<Instance<AttributesMap, Serializable>> getOutPredicate() {
		return new Predicate<Instance<AttributesMap, Serializable>>() {

			@Override
			public boolean apply(final Instance<AttributesMap, Serializable> input) {
				return !decide(input.getAttributes());
			}
		};
	}


	@Override
	public Leaf getLeaf(final AttributesMap attributes) {
		if (decide(attributes))
			return trueChild.getLeaf(attributes);
		else
			return falseChild.getLeaf(attributes);
	}

    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        if (attributesToIgnore.contains(this.attribute)) {
            return probabilityOfTrueChild * trueChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore) +
                    (1 - probabilityOfTrueChild) * falseChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
        } else {
            if (decide(attributes)) {
                return trueChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            }
            else {
                return falseChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            }
         }
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

