package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy<VC extends ValueCounter<VC>> {

    boolean shouldWeIgnoreThisValue(final VC valueCounts);

}
