package quickml.decisionTree.nodes;

import quickml.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.LeafBuilder;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public class DTLeafBuilder implements LeafBuilder<ClassificationCounter,DTNode> {
    public DTLeaf buildLeaf(Branch<ClassificationCounter, DTNode> parent, ClassificationCounter valueCounter){
        return new DTLeaf((DTBranch)parent, valueCounter, parent.isEmpty() ? 0 : parent.getDepth());
    }
}
