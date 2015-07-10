package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree;

import com.google.common.base.Predicate;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;


public abstract class OldBranch extends OldNode {
	private static final long serialVersionUID = 8290012786245422175L;

	public final String attribute;

	public OldNode trueChild, falseChild;
    private double probabilityOfTrueChild;

	public OldBranch(OldNode parent, final String attribute, double probabilityOfTrueChild) {
		super(parent);
        this.probabilityOfTrueChild = probabilityOfTrueChild;
        this.attribute = attribute;
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
	public OldLeaf getLeaf(final AttributesMap attributes) {
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
	protected void calcLeafDepthStats(final LeafDepthStats stats) {
		trueChild.calcLeafDepthStats(stats);
		falseChild.calcLeafDepthStats(stats);
	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OldBranch oldBranch = (OldBranch) o;

        if (!attribute.equals(oldBranch.attribute)) return false;
        if (!falseChild.equals(oldBranch.falseChild)) return false;
        if (!trueChild.equals(oldBranch.trueChild)) return false;

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

