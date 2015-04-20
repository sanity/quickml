package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.AttributeValueIgnoringStrategy;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<T extends InstanceWithAttributesMap, DS extends TermStatistics, A extends AttributeValueIgnoringStrategy<DS>> {
    AttributeValueIgnoringStrategyBuilder<T, DS, A> copy();
    A createAttributeValueIgnoringStrategy();
}
