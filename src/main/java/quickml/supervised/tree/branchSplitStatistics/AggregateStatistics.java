package quickml.supervised.tree.branchSplitStatistics;

import quickml.data.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AggregateStatistics <L, I extends InstanceWithAttributesMap<L>, TS extends ValueStatistics> {
    public abstract TS getAggregateStats(List<I> instances);
}
