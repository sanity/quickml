package quickml.supervised;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;

import java.io.Serializable;
import java.util.List;

/**
 * PredictiveModelBuilder that supports adding data to predictive models
 * The builder will keep track of the number of times the model has been built, and if the rebuild threshold is passed the model will be rebuilt
 * If the split node threshold is passed the leaves will be rebuilt
 */
public class PredictiveModelWithDataBuilder<INPUT, OUTPUT, PREDICTION, PM extends PredictiveModel<INPUT, OUTPUT>> implements UpdatablePredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM> {
    protected PM predictiveModel;
    private final UpdatablePredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM> updatablePredictiveModelBuilder;
    protected Integer rebuildThreshold; //Integer.valueOf(1);
    protected Integer splitNodeThreshold;// Integer.valueOf(1);
    protected int buildCount = 0;
    public List<Instance<INPUT, OUTPUT>> instances = Lists.newArrayList();


    public PredictiveModelWithDataBuilder(UpdatablePredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM> updatablePredictiveModelBuilder) {
        this(updatablePredictiveModelBuilder, null);
    }

    public PredictiveModelWithDataBuilder(UpdatablePredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM> updatablePredictiveModelBuilder, PM predictiveModel) {
        this.updatablePredictiveModelBuilder = updatablePredictiveModelBuilder;
        this.predictiveModel = predictiveModel;
        updatablePredictiveModelBuilder.updatable(true);
    }

    public PredictiveModelWithDataBuilder<INPUT, OUTPUT, PREDICTION, PM> rebuildThreshold(Integer rebuildThreshold) {
        this.rebuildThreshold = rebuildThreshold;
        return this;
    }

    public PredictiveModelWithDataBuilder<INPUT, OUTPUT, PREDICTION, PM> splitNodeThreshold(Integer splitNodeThreshold) {
        this.splitNodeThreshold = splitNodeThreshold;
        return this;
    }

    public PredictiveModelWithDataBuilder<INPUT, OUTPUT, PREDICTION, PM> updatable(boolean updatable) {
        return this;
    }

    @Override
    public PM buildPredictiveModel(Iterable<? extends Instance<INPUT, OUTPUT>> newData) {
        Iterables.addAll(instances, newData);
        if (rebuildThreshold != null || splitNodeThreshold != null) {
            buildCount++;
        }

        //check if we want to build a new predictive model or update existing
        if (predictiveModel == null || (rebuildThreshold != null && rebuildThreshold!= 0 && buildCount > rebuildThreshold)) {
            buildCount = 1;
            predictiveModel = buildUpdatablePredictiveModel(instances);

        } else {
            boolean splitNodes = splitNodeThreshold != null && splitNodeThreshold != 0 && buildCount % splitNodeThreshold == 0;
            updatePredictiveModel(predictiveModel, newData, splitNodes);
        }

        return predictiveModel;
    }

    private PM buildUpdatablePredictiveModel(Iterable<? extends Instance<INPUT, OUTPUT>> trainingData) {
        return updatablePredictiveModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(PM predictiveModel, Iterable<? extends Instance<INPUT, OUTPUT>> newData, boolean splitNodes) {
        updatablePredictiveModelBuilder.updatePredictiveModel(predictiveModel, newData, splitNodes);
    }

    @Override
    public void stripData(PM predictiveModel) {
        updatablePredictiveModelBuilder.stripData(predictiveModel);
    }

    @Override
    public void setID(Serializable id) {
        updatablePredictiveModelBuilder.setID(id);
    }
}
