package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public final class OldCategoricalOldBranch extends OldBranch {
	private static final long serialVersionUID = -1723969623146234761L;
	public final Set<Serializable> inSet;

	public OldCategoricalOldBranch(OldNode parent, final String attribute, final Set<Serializable> inSet, double probabilityOfTrueChild) {
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

        final OldCategoricalOldBranch that = (OldCategoricalOldBranch) o;

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
