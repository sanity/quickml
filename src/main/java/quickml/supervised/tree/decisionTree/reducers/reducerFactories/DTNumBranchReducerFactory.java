package quickml.supervised.tree.decisionTree.reducers.reducerFactories;

import quickml.data.instances.ClassifierInstance;
import quickml.supervised.tree.decisionTree.reducers.DTNumBranchReducer;
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
public class DTNumBranchReducerFactory<I extends ClassifierInstance> implements ReducerFactory<I, ClassificationCounter>{
   int numSamplesPerBin;
   int numNumericBins;


    @Override
    public Reducer<I, ClassificationCounter> getReducer(List<I> trainingData) {
        return new DTNumBranchReducer<>(trainingData, numSamplesPerBin, numNumericBins);
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
