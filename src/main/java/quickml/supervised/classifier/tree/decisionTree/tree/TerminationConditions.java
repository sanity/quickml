package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/4/15.
 */

//options get rid of the class and

    //isValidSplit, move to BranchFinderClass? Sure. Makes sense. except
public interface TerminationConditions  <L, T extends InstanceWithAttributesMap<L>, GS extends GroupStatistics> {
    //so should the BranchFinderBuildersGet their own termination conditions? Maybe.
    boolean isValidSplit(GS groupStatistics);  //needs a classification counter, and minLeafInstances.  SplitProperties can be whatever
    boolean canTryAddingChildren(Branch branch, List<T> instances);//Branch has score and depth info in itg
    double getMinScore();
    void update(Map<String, Object> cfg);
    TerminationConditions copy();
}
