package quickml.supervised.tree.decisionTree.reducers.reducerFactories;

import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.reducers.DTCatBranchReducer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.reducers.ReducerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.NUM_NUMERIC_BINS;
import static quickml.supervised.tree.constants.ForestOptions.NUM_SAMPLES_PER_NUMERIC_BIN;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class DTCatBranchReducerFactory<I extends ClassifierInstance> implements ReducerFactory<I, ClassificationCounter>{

    @Override
    public Reducer<I, ClassificationCounter> getReducer(List<I> trainingData) {
        return new DTCatBranchReducer<>(trainingData);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> cfg) {

    }
}
