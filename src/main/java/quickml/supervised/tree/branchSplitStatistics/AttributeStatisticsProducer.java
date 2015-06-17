package quickml.supervised.tree.branchSplitStatistics;


import quickml.supervised.tree.nodes.AttributeStats;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AttributeStatisticsProducer<TS extends ValueCounter<TS>> {
    AttributeStats<TS> getAttributeStats(String attribute);
}
