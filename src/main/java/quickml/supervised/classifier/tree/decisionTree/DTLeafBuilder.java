package quickml.supervised.classifier.tree.decisionTree;

import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.DTLeaf;
import quickml.supervised.classifier.tree.decisionTree.tree.LeafBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public class DTLeafBuilder implements LeafBuilder<ClassificationCounter>{
    public DTLeaf buildLeaf(Branch parent, ClassificationCounter termStatistics){
        return new DTLeaf(parent, termStatistics, parent.isEmpty() ? 0 : parent.depth);
    }
}
