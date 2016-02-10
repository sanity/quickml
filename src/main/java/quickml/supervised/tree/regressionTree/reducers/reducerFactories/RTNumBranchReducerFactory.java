package quickml.supervised.tree.regressionTree.reducers.reducerFactories;

import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.reducers.ReducerFactory;
import quickml.supervised.tree.regressionTree.reducers.RTNumBranchReducer;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.NUM_NUMERIC_BINS;
import static quickml.supervised.tree.constants.ForestOptions.NUM_SAMPLES_PER_NUMERIC_BIN;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class RTNumBranchReducerFactory<I extends RegressionInstance> implements ReducerFactory<I, MeanValueCounter>{
   int numSamplesPerBin;
   int numNumericBins;


    @Override
    public Reducer<I, MeanValueCounter> getReducer(List<I> trainingData) {
        return new RTNumBranchReducer<>(trainingData, numSamplesPerBin, numNumericBins);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> cfg) {
        if (cfg.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())) {
            numSamplesPerBin = (int) cfg.get(NUM_SAMPLES_PER_NUMERIC_BIN.name());
        }
        if (cfg.containsKey(NUM_NUMERIC_BINS.name())) {
            numNumericBins = (int) cfg.get(NUM_NUMERIC_BINS.name());
        }
    }
}
