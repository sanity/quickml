package quickml.decisionTree.reducers;

import quickml.data.ClassifierInstance;
import quickml.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.Reducer;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public abstract class DTreeReducer<I extends ClassifierInstance> extends Reducer<I, ClassificationCounter> {

}
