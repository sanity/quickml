package quickml.supervised.tree.decisionTree;

import quickml.supervised.tree.decisionTree.tree.LeafBuilder;
import quickml.supervised.tree.nodes.Branch;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public class DTLeafBuilder implements LeafBuilder<ClassificationCounter>{
    public DTLeaf buildLeaf(Branch parent, ClassificationCounter termStatistics){
        return new DTLeaf(parent, termStatistics, parent.isEmpty() ? 0 : parent.depth);
    }
}
