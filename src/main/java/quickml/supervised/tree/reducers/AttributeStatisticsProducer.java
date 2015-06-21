package quickml.supervised.tree.reducers;


import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AttributeStatisticsProducer<VC extends ValueCounter<VC>> {
    AttributeStats<VC> getAttributeStats(String attribute);
}
