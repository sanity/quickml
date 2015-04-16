package quickml.supervised.classifier.tree.decisionTree.tree.nodes;

import com.google.common.base.Predicate;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.classifier.tree.decisionTree.tree.GroupStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.Leaf;
import quickml.supervised.classifier.tree.decisionTree.tree.Node;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;


public abstract class Branch<GS extends GroupStatistics> extends Node {
	private static final long serialVersionUID = 8290012786245422175L;

	public final String attribute;
	public Node trueChild, falseChild;
    //should put in node that implements: ModelWithIgnorableAttributes
    private double probabilityOfTrueChild;
    public double score;
    public int depth;
    public GS groupStatistics;

	public Branch(Branch parent, final String attribute, double probabilityOfTrueChild, double score, GS groupStatistics) {
		super(parent);
        this.probabilityOfTrueChild = probabilityOfTrueChild;
        this.attribute = attribute;
        this.depth =  (parent!=null) ? this.depth = parent.depth + 1 : 0;
        this.score = score;
        this.groupStatistics = groupStatistics;
	}

	public abstract boolean decide(Map<String, Serializable> attributes);

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
        //TODO[mk] - check with Alex
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
	public void calcMeanDepth(final LeafDepthStats stats) {
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

