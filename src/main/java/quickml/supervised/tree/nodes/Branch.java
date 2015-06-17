package quickml.supervised.tree.nodes;

import com.google.common.base.Predicate;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;


import java.io.Serializable;
import java.util.Map;


public abstract class Branch<VC extends ValueCounter<VC>> implements Node<VC>, Serializable {
	private static final long serialVersionUID = 8290012786245422175L;
	public final String attribute;
	public Node<VC> trueChild, falseChild;
    public VC termStatistics;
    protected Node<VC> parent;
	protected final double probabilityOfTrueChild;
	public final double score;
	protected final int depth;

	public Branch(Branch<VC> parent, final String attribute, double probabilityOfTrueChild, double score, VC termStatistics) {
        this.parent = parent;
        this.attribute = attribute;
        this.depth = (parent!=null) ? parent.depth + 1 : 0;
        this.score = score;
        this.termStatistics = termStatistics;
		this.probabilityOfTrueChild = probabilityOfTrueChild;
	}

	public Node<VC> getTrueChild(){
        return trueChild;
    }

    public Node<VC> getFalseChild(){
        return trueChild;
    }

    public int getDepth() {
        return depth;
    }

    public VC getTermStatistics() {
        return termStatistics;
    }

    public double getProbabilityOfTrueChild() {
        return probabilityOfTrueChild;
    }

    public double getScore() {
        return score;
    }

    public Node<VC> getParent() {
        return parent;
    }

    public boolean isEmpty() {
        return attribute.isEmpty();
    }

	public abstract boolean decide(Map<String, Object> attributes);

	@Override
	public int getSize() {
		return 1 + trueChild.getSize() + falseChild.getSize();
	}

	public Predicate<Instance<AttributesMap, Object>> getInPredicate() {
		return new Predicate<Instance<AttributesMap, Object>>() {

			@Override
			public boolean apply(final Instance<AttributesMap, Object> input) {
				return decide(input.getAttributes());
			}
		};
	}

	@Override
	public void calcMeanDepth(final LeafDepthStats stats) {
		trueChild.calcMeanDepth(stats);
		falseChild.calcMeanDepth(stats);
	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Branch<VC> branch = (Branch<VC>) o;

        if (!attribute.equals(branch.attribute)) return false;
        if (!falseChild.equals(branch.falseChild)) return false;
        if (!trueChild.equals(branch.trueChild)) return false;

        return true;
    }

	//TODO: this is wildly inefficient
    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        result = 31 * result + trueChild.hashCode();
        result = 31 * result + falseChild.hashCode();
        return result;
    }
}

