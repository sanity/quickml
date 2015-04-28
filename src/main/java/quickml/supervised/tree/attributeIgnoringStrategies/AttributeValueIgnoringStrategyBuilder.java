package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface AttributeValueIgnoringStrategyBuilder<TS extends TermStatsAndOperations<TS>, D extends DataProperties> {
    AttributeValueIgnoringStrategyBuilder<TS, D> copy();
    AttributeValueIgnoringStrategy<TS> createAttributeValueIgnoringStrategy(D dataProperties);
}
