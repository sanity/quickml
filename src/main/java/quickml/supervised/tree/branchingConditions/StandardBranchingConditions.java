package quickml.supervised.tree.branchingConditions;

import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.decisionTree.nodes.DTNode;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import static quickml.supervised.tree.constants.ForestOptions.*;
import java.util.Map;


/**
 * Created by alexanderhawk on 4/4/15.
 */
public class StandardBranchingConditions<VC extends ValueCounter<VC>, N extends Node<VC, N>> implements BranchingConditions<VC, N> {
    private double minScore=0;
    private int maxDepth = Integer.MAX_VALUE;
    private int minLeafInstances = 0;
    private double minSplitFraction = 0;

    public StandardBranchingConditions(double minScore, int maxDepth, int minLeafInstances, double minSplitFraction) {
        this.minScore = minScore;
        this.maxDepth = maxDepth;
        this.minLeafInstances = minLeafInstances;
        this.minSplitFraction = minSplitFraction;
    }

    public StandardBranchingConditions() {}

    public double getMinScore() {
        return minScore;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMinLeafInstances() {
        return minLeafInstances;
    }

    public double getMinSplitFraction() {
        return minSplitFraction;
    }

    public StandardBranchingConditions minScore(double minScore) {
        this.minScore = minScore;
        return this;
    }

    public StandardBranchingConditions maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;

    }

    public StandardBranchingConditions minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }


    @Override
    public boolean isInvalidSplit(VC trueSet, VC falseSet) {
        double splitFraction = Math.min(trueSet.getTotal(), falseSet.getTotal())/ (trueSet.getTotal() + falseSet.getTotal());
        return splitFraction < minSplitFraction || trueSet.getTotal() < minLeafInstances
                || falseSet.getTotal() < minLeafInstances;
    }

    public boolean isInvalidSplit(double score) {
        return score <= minScore;
    }

    @Override
    public boolean canTryAddingChildren(Branch<VC, N> parent, VC totals){
        return parent.getDepth() < maxDepth && totals.getTotal() > 2 * minLeafInstances;
    }

    @Override
    public void update(Map<String, Object> cfg) {
        if (cfg.containsKey(MAX_DEPTH.name()))
            maxDepth = (Integer) cfg.get(MAX_DEPTH.name());
        if (cfg.containsKey(MIN_SCORE.name()))
            minScore = (Double) cfg.get(MIN_SCORE.name());
        if (cfg.containsKey(MIN_LEAF_INSTANCES.name()))
            minLeafInstances = (Integer) cfg.get(MIN_LEAF_INSTANCES.name());
        if (cfg.containsKey(MIN_SLPIT_FRACTION.name()))
            minSplitFraction = (Double) cfg.get(MIN_SLPIT_FRACTION.name());
    }

    @Override
    public StandardBranchingConditions copy(){
        return new StandardBranchingConditions(this.minScore, this.maxDepth, this.minLeafInstances, this.minSplitFraction);
    }


}
