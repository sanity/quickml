package quickml.supervised.tree.summaryStatistics;


import quickml.supervised.tree.nodes.AttributeStats;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AttributeStatisticsProducer<VC extends ValueCounter<VC>> {
    AttributeStats<VC> getAttributeStats(String attribute);
}
