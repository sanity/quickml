package quickml.supervised.tree.branchingConditions;

import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Node;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/4/15.
 */


public interface BranchingConditions<VC extends ValueCounter<VC>> {
    boolean isInvalidSplit(VC trueValueStats, VC falseValueStats);

    boolean isInvalidSplit(double score);

    boolean isInvalidSplit(VC trueSet, VC falseSet, String attribute);

    boolean canTryAddingChildren(Branch<VC> branch, VC VC);

    void update(Map<String, Object> cfg);

    BranchingConditions copy();
}