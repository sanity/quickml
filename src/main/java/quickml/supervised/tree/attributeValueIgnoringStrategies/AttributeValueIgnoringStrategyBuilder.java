package quickml.supervised.tree.attributeValueIgnoringStrategies;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<VC extends ValueCounter<VC>> extends Serializable{
    AttributeValueIgnoringStrategyBuilder<VC> copy();
    AttributeValueIgnoringStrategy<VC> createAttributeValueIgnoringStrategy(VC valueCounts);
}
