package quickml.supervised.tree.regressionTree.reducers.reducerFactories;

import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.decisionTree.reducers.DTBinaryCatBranchReducer;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.reducers.ReducerFactory;
import quickml.supervised.tree.regressionTree.reducers.RTCatBranchReducer;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class RTCatBranchReducerFactory<I extends RegressionInstance> implements ReducerFactory<I, MeanValueCounter>{

    public RTCatBranchReducerFactory() {
    }

    @Override
    public Reducer<I, MeanValueCounter> getReducer(List<I> trainingData) {
        return new RTCatBranchReducer<>(trainingData);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> cfg) {

    }
}
