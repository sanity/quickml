package quickml.supervised.tree.nodes;

import com.google.common.base.Predicate;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.decisionTree.DTLeaf;


import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;


public abstract class Branch<TS extends TermStatsAndOperations<TS>> extends Node<TS> implements Serializable {
	private static final long serialVersionUID = 8290012786245422175L;
	public final String attribute;
	private Node<TS> trueChild, falseChild;
    //should put in node that implements: ModelWithIgnorableAttributes
    private double probabilityOfTrueChild;
    public double score = Scorer.NO_SCORE;
    public int depth;
    public TS termStatistics;
    private Node<TS> parent;

	public Branch(Branch<TS> parent, final String attribute, double probabilityOfTrueChild, double score, TS termStatistics) {
        this.parent = parent;
        this.probabilityOfTrueChild = probabilityOfTrueChild;
        this.attribute = attribute;
        this.depth =  (parent!=null) ? this.depth = parent.depth + 1 : 0;
        this.score = score;
        this.termStatistics = termStatistics;
	}

    public Node<TS> getTrueChild(){
        return trueChild;
    }

    public Node<TS> getFalseChild(){
        return trueChild;
    }

    public Node<TS> getParent() {
        return parent;
    }


    public boolean isEmpty() {
        return attribute.isEmpty();
    }

	public abstract boolean decide(Map<String, Object> attributes);

	@Override
	public int size() {
		return 1 + trueChild.size() + falseChild.size();
	}

	public Predicate<Instance<AttributesMap, Object>> getInPredicate() {
		return new Predicate<Instance<AttributesMap, Object>>() {

			@Override
			public boolean apply(final Instance<AttributesMap, Object> input) {
				return decide(input.getAttributes());
			}
		};
	}

	public Predicate<Instance<AttributesMap, Object>> getOutPredicate() {
		return new Predicate<Instance<AttributesMap, Object>>() {

			@Override
			public boolean apply(final Instance<AttributesMap, Object> input) {
				return !decide(input.getAttributes());
			}
		};
	}


	@Override
	public Leaf<TS> getLeaf(final AttributesMap attributes) {
		if (decide(attributes))
			return trueChild.getLeaf(attributes);
		else
			return falseChild.getLeaf(attributes);
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

        final Branch<TS> branch = (Branch<TS>) o;

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

