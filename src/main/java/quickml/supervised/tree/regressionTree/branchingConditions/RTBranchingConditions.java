package quickml.supervised.tree.regressionTree.branchingConditions;

import quickml.supervised.tree.branchingConditions.StandardBranchingConditions;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public class RTBranchingConditions extends StandardBranchingConditions<MeanValueCounter>{
    public RTBranchingConditions(double minScore, int maxDepth, int minLeafInstances, double minSplitFraction) {
        super(minScore, maxDepth, minLeafInstances, minSplitFraction);
    }

    public RTBranchingConditions() {
    }
}
