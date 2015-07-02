package quickml.supervised.tree.branchingConditions;

import com.google.common.collect.Sets;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import static quickml.supervised.tree.constants.ForestOptions.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;


/**
 * Created by alexanderhawk on 4/4/15.
 */
public class StandardBranchingConditions<VC extends ValueCounter<VC>> implements BranchingConditions<VC> {
    private double minScore=0;
    private int maxDepth = Integer.MAX_VALUE;
    private int minLeafInstances = 0;
    private double minSplitFraction = 0;
    private Set<String> exemptAttributes = Sets.newHashSet();


    public StandardBranchingConditions(double minScore, int maxDepth, int minLeafInstances, double minSplitFraction) {
        this(minScore, maxDepth, minLeafInstances, minSplitFraction, Sets.<String>newHashSet());
    }

    public StandardBranchingConditions(double minScore, int maxDepth, int minLeafInstances, double minSplitFraction, Set<String> exemptAttributes) {
        this.minScore = minScore;
        this.maxDepth = maxDepth;
        this.minLeafInstances = minLeafInstances;
        this.minSplitFraction = minSplitFraction;
        this.exemptAttributes = exemptAttributes;
    }

    public StandardBranchingConditions() {}

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

    public StandardBranchingConditions minSplitFraction(double minSplitFraction) {
        this.minSplitFraction = minSplitFraction;
        return this;
    }

    public StandardBranchingConditions exemptAttributes(Set<String> exemptAttributes) {
        this.exemptAttributes = exemptAttributes;
        return this;
    }

    @Override
    public boolean isInvalidSplit(VC trueSet, VC falseSet, String attribute) {
        return ( getSplitFraction(trueSet, falseSet) < minSplitFraction && !exemptAttributes.contains(attribute)) || violatesMinLeafInstances(trueSet, falseSet);
    }

    @Override
    public boolean isInvalidSplit(VC trueSet, VC falseSet) {
        return (getSplitFraction(trueSet, falseSet) < minSplitFraction) || violatesMinLeafInstances(trueSet, falseSet);
    }

    private double getSplitFraction(VC trueSet, VC falseSet) {
        return Math.min(trueSet.getTotal(), falseSet.getTotal())/ (trueSet.getTotal() + falseSet.getTotal());
    }

    private boolean violatesMinLeafInstances(VC trueSet, VC falseSet) {
        return trueSet.getTotal() < minLeafInstances
                || falseSet.getTotal() < minLeafInstances;
    }





    public boolean isInvalidSplit(double score) {
        return score <= minScore;
    }

    @Override
    public boolean canTryAddingChildren(Branch<VC> parent, VC totals){
        return (parent==null || parent.getDepth() < maxDepth) && totals.getTotal() > 2 * minLeafInstances;
    }

    @Override
    public void update(Map<String, Serializable> cfg) {
        if (cfg.containsKey(MAX_DEPTH.name()))
            maxDepth = (Integer) cfg.get(MAX_DEPTH.name());
        if (cfg.containsKey(MIN_SCORE.name()))
            minScore = (Double) cfg.get(MIN_SCORE.name());
        if (cfg.containsKey(MIN_LEAF_INSTANCES.name()))
            minLeafInstances = (Integer) cfg.get(MIN_LEAF_INSTANCES.name());
        if (cfg.containsKey(MIN_SLPIT_FRACTION.name()))
            minSplitFraction = (Double) cfg.get(MIN_SLPIT_FRACTION.name());
        if (cfg.containsKey(EXEMPT_ATTRIBUTES.name()))
            exemptAttributes = (Set<String>) cfg.get(EXEMPT_ATTRIBUTES.name());
    }

    @Override
    public synchronized StandardBranchingConditions copy(){
        return new StandardBranchingConditions(this.minScore, this.maxDepth, this.minLeafInstances, this.minSplitFraction, Sets.newHashSet(this.exemptAttributes));
    }


}
