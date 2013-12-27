package quickdt;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

public final class NominalBranch extends Branch {
	private static final long serialVersionUID = -1723969623146234761L;
	public final Set<Serializable> inSet;

	public NominalBranch(Node parent, final String attribute, final Set<Serializable> inSet) {
		super(parent, attribute);
		this.inSet = Sets.newHashSet(inSet);

	}

	@Override
	protected boolean decide(final Attributes attributes) {
		return inSet.contains(attributes.get(attribute));
	}

	@Override
	public String toString() {
		return attribute + " in " + inSet;
	}

	@Override
	public String toNotString() {
		return attribute + " not in " + inSet;
	}
}
