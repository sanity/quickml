package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatsAndOperations;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<TS extends TermStatsAndOperations<TS>, D extends DataProperties> {
    AttributeValueIgnoringStrategyBuilder<TS, D> copy();
    AttributeValueIgnoringStrategy<TS> createAttributeValueIgnoringStrategy(D dataProperties);
}
