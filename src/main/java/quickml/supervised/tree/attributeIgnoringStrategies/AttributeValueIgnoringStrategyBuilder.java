package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<TS extends ValueCounter<TS>, D extends DataProperties> {
    AttributeValueIgnoringStrategyBuilder<TS, D> copy();
    AttributeValueIgnoringStrategy<TS> createAttributeValueIgnoringStrategy(D dataProperties);
}
