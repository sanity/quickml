package quickml.supervised.tree.nodes;


import quickml.supervised.tree.branchSplitStatistics.ValueCounter;

import java.util.List;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public class AttributeStats<TS extends ValueCounter<TS>> {
    List<TS> termStats;
    TS aggregateStats;
    String attribute;

    public AttributeStats(List<TS> termStats, TS aggregateStats, String attribute) {
        this.termStats = termStats;
        this.aggregateStats = aggregateStats;
        this.attribute = attribute;
    }

    public List<TS> getTermStats() {
        return termStats;
    }
    public TS getAggregateStats() {
        return aggregateStats;
    }

    public String getAttribute() {
        return attribute;
    }
}
