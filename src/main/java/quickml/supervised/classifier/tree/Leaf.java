package quickml.supervised.classifier.tree;

import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatisticsOperations;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatsAndOperations;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public interface Leaf<TS extends TermStatsAndOperations<TS>> {
    TS getTermStats();
}
