package quickml.supervised.tree.reducers;


import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.util.List;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public class AttributeStats<VC extends ValueCounter<VC>> {
    List<VC> attributeValueStatsList;
    VC aggregateStats;
    String attribute;

    public AttributeStats(List<VC> termStats, VC aggregateStats, String attribute) {
        this.attributeValueStatsList = termStats;
        this.aggregateStats = aggregateStats;
        this.attribute = attribute;
    }

    public List<VC> getStatsOnEachValue() {
        return attributeValueStatsList;
    }
    public VC getAggregateStats() {
        return aggregateStats;
    }

    public String getAttribute() {
        return attribute;
    }
}
