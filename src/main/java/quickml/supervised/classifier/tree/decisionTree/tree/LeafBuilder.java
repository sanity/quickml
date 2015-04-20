package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<GS extends TermStatistics> {
    Leaf buildLeaf(Branch parent, TermStatistics termStatistics);
}
