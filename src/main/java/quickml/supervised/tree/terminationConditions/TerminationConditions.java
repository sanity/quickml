package quickml.supervised.tree.terminationConditions;

import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.branchFinders.TermStatsAndOperations;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/4/15.
 */

//options get rid of the class and

    //isInvalidSplit, move to BranchFinderClass? Sure. Makes sense. except
public interface TerminationConditions  <TS extends TermStatsAndOperations<TS>> {
    //so should the BranchFinderBuildersGet their own termination conditions? Maybe.
    boolean isInvalidSplit(TS trueTermStats, TS falseTermStats);  //needs a classification counter, and minLeafInstances.  SplitProperties can be whatever
    boolean canTryAddingChildren(Branch branch, TS ts);//Branch has score and depth info in itg
    double getMinScore();
    int getMaxDepth();
    int getMinLeafInstances();
    void update(Map<String, Object> cfg);
    TerminationConditions copy();
}
