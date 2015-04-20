package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/4/15.
 */

//options get rid of the class and

    //isValidSplit, move to BranchFinderClass? Sure. Makes sense. except
public interface TerminationConditions  <GS extends TermStatistics> {
    //so should the BranchFinderBuildersGet their own termination conditions? Maybe.
    boolean isValidSplit(GS groupStatistics);  //needs a classification counter, and minLeafInstances.  SplitProperties can be whatever
    boolean canTryAddingChildren(Branch branch, GS gs);//Branch has score and depth info in itg
    double getMinScore();
    int getMaxDepth();
    void update(Map<String, Object> cfg);
    TerminationConditions copy();
}
