package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.MAX_DEPTH;
import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.MIN_LEAF_INSTANCES;
import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.MIN_SCORE;

/**
 * Created by alexanderhawk on 4/4/15.
 */
public class StandardTerminationConditions<T extends InstanceWithAttributesMap> implements TerminationConditions<T, StandardTerminationConditions.StandardSplitProperties> {
    private double minScore=0;
    private int maxDepth = Integer.MAX_VALUE;
    private int minLeafInstances = 0;

    public StandardTerminationConditions(double minScore, int maxDepth, int minLeafInstances) {
        this.minScore = minScore;
        this.maxDepth = maxDepth;
        this.minLeafInstances = minLeafInstances;
    }

    public double getMinScore() {
        return minScore;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMinLeafInstances() {
        return minLeafInstances;
    }

    @Override
    public boolean isValidSplit(StandardSplitProperties standardSplitProperties) {
        return standardSplitProperties.numInSetInstances > minLeafInstances
                && standardSplitProperties.numOutSetInstances > minLeafInstances;
    }

    @Override
    public boolean canTryAddingChildren(Branch branch, List<T> instances){
        return branch.depth < maxDepth && instances.size() > 2 * minLeafInstances;
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

    public static class StandardSplitProperties implements SplitProperties{
        public int numInSetInstances;
        public int numOutSetInstances;

        public StandardSplitProperties(int inSetInstances, int outSetInstances) {
            this.numInSetInstances = inSetInstances;
            this.numOutSetInstances = outSetInstances;
        }
    }

}
