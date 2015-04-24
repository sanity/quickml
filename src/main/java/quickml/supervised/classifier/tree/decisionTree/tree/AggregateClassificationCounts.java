package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.ClassifierInstance;

import java.util.List;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class AggregateClassificationCounts<T extends ClassifierInstance> implements  AggregateStatistics<Object, T, ClassificationCounter>{
    @Override
    public ClassificationCounter getAggregateStats(List<T> instances) {
        return ClassificationCounter.countAll(instances);
    }
}
