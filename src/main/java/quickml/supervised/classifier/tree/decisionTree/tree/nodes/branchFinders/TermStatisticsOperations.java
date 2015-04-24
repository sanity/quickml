package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public interface TermStatisticsOperations<TS extends TermStatistics> {
    TS add(TS ts);
    TS subtract(TS ts);
}
