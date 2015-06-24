package quickml.decisionTree.branchingConditions;

import quickml.supervised.tree.branchingConditions.StandardBranchingConditions;
import quickml.decisionTree.nodes.DTNode;
import quickml.decisionTree.valueCounters.ClassificationCounter;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public class DTBranchingConditions extends StandardBranchingConditions<ClassificationCounter, DTNode>{
    public DTBranchingConditions(double minScore, int maxDepth, int minLeafInstances, double minSplitFraction) {
        super(minScore, maxDepth, minLeafInstances, minSplitFraction);
    }

    public DTBranchingConditions() {
    }
}
