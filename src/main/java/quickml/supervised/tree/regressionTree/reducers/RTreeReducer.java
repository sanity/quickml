package quickml.supervised.tree.regressionTree.reducers;

import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.util.List;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public abstract class RTreeReducer<I extends RegressionInstance> extends Reducer<I, MeanValueCounter> {
    public RTreeReducer(List<I> trainingData) {
        super(trainingData);
    }
}
