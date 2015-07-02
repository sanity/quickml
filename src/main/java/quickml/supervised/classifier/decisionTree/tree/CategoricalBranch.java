package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.collect.Sets;
import quickml.data.AttributesMap;

import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public final class CategoricalBranch extends Branch {
	private static final long serialVersionUID = -1723969623146234761L;
	public final Set<Serializable> inSet;

	public CategoricalBranch(Node parent, final String attribute, final Set<Serializable> inSet, double probabilityOfTrueChild) {
		super(parent, attribute, probabilityOfTrueChild);
		this.inSet = Sets.newHashSet(inSet);

	}

    @Override
    public boolean decide(final Map<String, Serializable> attributes) {
        Serializable attributeVal = attributes.get(attribute);
        //missing values always go the way of the outset...which strangely seems to be most accurate
        return inSet.contains(attributeVal);
    }

	@Override
	public String toString() {
		return attribute + " in " + inSet;
	}

	@Override
	public String toNotString() {
		return attribute + " not in " + inSet;
	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final CategoricalBranch that = (CategoricalBranch) o;

        if (!inSet.equals(that.inSet)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + inSet.hashCode();
        return result;
    }
}
