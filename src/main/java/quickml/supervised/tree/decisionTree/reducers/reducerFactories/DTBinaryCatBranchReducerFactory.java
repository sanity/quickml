package quickml.supervised.tree.decisionTree.reducers.reducerFactories;

import quickml.data.instances.ClassifierInstance;
import quickml.supervised.tree.decisionTree.reducers.DTBinaryCatBranchReducer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.reducers.ReducerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class DTBinaryCatBranchReducerFactory<I extends ClassifierInstance> implements ReducerFactory<I, ClassificationCounter>{
    private final Serializable minorityClassification;

    public DTBinaryCatBranchReducerFactory(Serializable minorityClassification) {
        this.minorityClassification = minorityClassification;
    }

    @Override
    public Reducer<I, ClassificationCounter> getReducer(List<I> trainingData) {
        return new DTBinaryCatBranchReducer<>(trainingData, minorityClassification);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> cfg) {

    }
}
