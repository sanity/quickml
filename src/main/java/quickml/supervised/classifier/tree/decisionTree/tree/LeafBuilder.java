package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.tree.Leaf;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatsAndOperations;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<TS extends TermStatsAndOperations<TS>> {
    Leaf<TS> buildLeaf(Branch parent, TS termStatistics);
}
