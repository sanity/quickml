package quickml.supervised.tree.decisionTree.nodes;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.LeafBuilder;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public class DTLeafBuilder implements LeafBuilder<ClassificationCounter> {
    public DTLeaf buildLeaf(Branch<ClassificationCounter> parent, ClassificationCounter valueCounter){
        return new DTLeaf(parent, valueCounter, parent.isEmpty() ? 0 : parent.getDepth());
    }
}
