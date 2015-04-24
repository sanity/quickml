package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<TS extends TermStatistics, D extends DataProperties> {
    AttributeValueIgnoringStrategyBuilder<TS, D> copy();
    AttributeValueIgnoringStrategy<TS> createAttributeValueIgnoringStrategy(D dataProperties);
}
