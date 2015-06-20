package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<VC extends ValueCounter<VC>> {
    AttributeValueIgnoringStrategyBuilder<VC> copy();
    AttributeValueIgnoringStrategy<VC> createAttributeValueIgnoringStrategy(VC valueCounts);
}
