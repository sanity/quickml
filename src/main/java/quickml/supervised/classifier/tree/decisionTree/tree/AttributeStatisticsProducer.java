package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AttributeStatisticsProducer<TS extends TermStatistics> {
    AttributeStats<TS> getAttributeStats(String attribute);
}
