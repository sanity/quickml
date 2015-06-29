package quickml.supervised.tree.reducers;


import com.google.common.base.Optional;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AttributeStatisticsProducer<VC extends ValueCounter<VC>> {
    Optional<AttributeStats<VC>> getAttributeStats(String attribute);
}
