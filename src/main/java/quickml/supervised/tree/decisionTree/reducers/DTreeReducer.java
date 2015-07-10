package quickml.supervised.tree.decisionTree.reducers;

import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.Reducer;

import java.util.List;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public abstract class DTreeReducer<I extends ClassifierInstance> extends Reducer<I, ClassificationCounter> {
    public DTreeReducer(List<I> trainingData) {
        super(trainingData);
    }
}
