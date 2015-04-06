package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/4/15.
 */
public interface TerminationConditions<T extends InstanceWithAttributesMap, S extends SplitProperties> {
    boolean isValidSplit(S splitProperties);  //needs a classification counter, and minLeafInstances.  SplitProperties can be whatever
    boolean canTryAddingChildren(Branch branch, List<T> instances);//Branch has score and depth info in itg
    double getMinScore();
    void update(Map<String, Object> cfg);
    TerminationConditions copy();
}
