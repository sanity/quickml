package quickml.supervised.tree.regressionTree.valueCounters;

import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;

import java.util.List;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class MeanValueCounterProducer<I extends RegressionInstance> implements ValueCounterProducer<I, MeanValueCounter> {
    @Override
    public MeanValueCounter getValueCounter(List<I> instances) {
        return MeanValueCounter.accumulateAll(instances);
    }
}
