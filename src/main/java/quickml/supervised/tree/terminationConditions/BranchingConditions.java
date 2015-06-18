package quickml.supervised.tree.terminationConditions;

import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Node;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/4/15.
 */


public abstract class BranchingConditions<VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    public abstract boolean isInvalidSplit(VC trueValueStats, VC falseValueStats);  //needs a classification counter, and minLeafInstances.  SplitProperties can be whatever

    public boolean isInvalidSplit(double score) {
        return score <= minScore;
    }

    public abstract boolean canTryAddingChildren(Branch<VC, N> branch, VC VC);//Branch has score and depth info in itg

    protected double minScore;
    protected int maxDepth;
    protected int minLeafInstances;


    public abstract void update(Map<String, Object> cfg);

    public abstract BranchingConditions copy();
}