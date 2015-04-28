package quickml.supervised.tree.terminationConditions;

import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;

import java.util.Map;

import static quickml.supervised.tree.decisionTree.tree.ForestOptions.MAX_DEPTH;
import static quickml.supervised.tree.decisionTree.tree.ForestOptions.MIN_LEAF_INSTANCES;
import static quickml.supervised.tree.decisionTree.tree.ForestOptions.MIN_SCORE;

/**
 * Created by alexanderhawk on 4/4/15.
 */
public class StandardTerminationConditions implements TerminationConditions<ClassificationCounter> {
    private double minScore=0;
    private int maxDepth = Integer.MAX_VALUE;
    private int minLeafInstances = 0;

    public StandardTerminationConditions(double minScore, int maxDepth, int minLeafInstances) {
        this.minScore = minScore;
        this.maxDepth = maxDepth;
        this.minLeafInstances = minLeafInstances;
    }

    public StandardTerminationConditions() {}

    public double getMinScore() {
        return minScore;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMinLeafInstances() {
        return minLeafInstances;
    }

    public StandardTerminationConditions minScore(double minScore) {
        this.minScore = minScore;
        return this;
    }

    public StandardTerminationConditions maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;

    }

    public StandardTerminationConditions minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }


    @Override
    public boolean isInvalidSplit(ClassificationCounter trueSet, ClassificationCounter falseSet) {
        return trueSet.getTotal() < minLeafInstances
                || falseSet.getTotal() < minLeafInstances;
    }

    @Override
    public boolean canTryAddingChildren(Branch parent, ClassificationCounter totals){
        return parent.depth < maxDepth && totals.getTotal() > 2 * minLeafInstances;
    }

    @Override
    public void update(Map<String, Object> cfg) {
        if (cfg.containsKey(MAX_DEPTH.name()))
            maxDepth = (Integer) cfg.get(MAX_DEPTH.name());
        if (cfg.containsKey(MIN_SCORE.name()))
            minScore = (Double) cfg.get(MIN_SCORE.name());
        if (cfg.containsKey(MIN_LEAF_INSTANCES.name()))
            minLeafInstances = (Integer) cfg.get(MIN_LEAF_INSTANCES.name());
    }

    @Override
    public StandardTerminationConditions copy(){
        return new StandardTerminationConditions(this.minScore, this.maxDepth, this.minLeafInstances);
    }


}
