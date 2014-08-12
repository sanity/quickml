package quickml.supervised.classifier;

import quickml.data.Instance;

import java.io.Serializable;

/**
 * PredictiveModelBuilder that supports adding data to predictive models
 * The builder will keep track of the number of times the model has been built, and if the rebuild threshold is passed the model will be rebuilt
 * If the split node threshold is passed the leaves will be rebuilt
 */
public class PredictiveModelWithDataBuilder<R, PM extends PredictiveModel<R,?>> implements UpdatablePredictiveModelBuilder<R, PM> {
    protected PM predictiveModel;
    private final UpdatablePredictiveModelBuilder<R, PM> updatablePredictiveModelBuilder;
    protected Integer rebuildThreshold;
    protected Integer splitNodeThreshold;
    protected int buildCount = 0;

    public PredictiveModelWithDataBuilder(UpdatablePredictiveModelBuilder<R, PM> updatablePredictiveModelBuilder) {
        this(updatablePredictiveModelBuilder, null);
    }

    public PredictiveModelWithDataBuilder(UpdatablePredictiveModelBuilder<R,PM> updatablePredictiveModelBuilder, PM predictiveModel) {
        this.updatablePredictiveModelBuilder = updatablePredictiveModelBuilder;
        this.predictiveModel = predictiveModel;
        updatablePredictiveModelBuilder.updatable(true);
    }

    public PredictiveModelWithDataBuilder<R, PM> rebuildThreshold(Integer rebuildThreshold) {
        this.rebuildThreshold = rebuildThreshold;
        return this;
    }

    public PredictiveModelWithDataBuilder<R, PM> splitNodeThreshold(Integer splitNodeThreshold) {
        this.splitNodeThreshold = splitNodeThreshold;
        return this;
    }

    public PredictiveModelWithDataBuilder<R,PM> updatable(boolean updatable) {
        return this;
    }

    @Override
    public PM buildPredictiveModel(Iterable<Instance<R>> newData) {
        if (rebuildThreshold != null || splitNodeThreshold != null) {
            buildCount++;
        }

        //check if we want to build a new predictive model or update existing
        if (predictiveModel == null || (rebuildThreshold != null && rebuildThreshold != 0 && buildCount > rebuildThreshold)) {
            buildCount = 1;
            predictiveModel = buildUpdatablePredictiveModel(newData);
        } else {
            boolean splitNodes = splitNodeThreshold != null && splitNodeThreshold != 0 && buildCount % splitNodeThreshold == 0;
            updatePredictiveModel(predictiveModel, newData, splitNodes);
        }

        return predictiveModel;
    }


    private PM buildUpdatablePredictiveModel(Iterable< Instance<R>> trainingData) {
        return updatablePredictiveModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(PM predictiveModel, Iterable<Instance<R>> newData, boolean splitNodes) {
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
