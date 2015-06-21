package quickml.supervised.tree.decisionTree.valueCounters;

import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;

import java.util.List;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class ClassificationCounterProducer<T extends ClassifierInstance> implements ValueCounterProducer<Object, T, ClassificationCounter> {
    @Override
    public ClassificationCounter getValueCounter(List<T> instances) {
        return ClassificationCounter.countAll(instances);
    }
}
