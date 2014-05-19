package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

public abstract class UpdatablePredictiveModelBuilder<PM extends PredictiveModel> implements PredictiveModelBuilder<PM> {
    protected PM predictiveModel;

    public UpdatablePredictiveModelBuilder(PM predictiveModel) {
        this.predictiveModel = predictiveModel;
    }
    public abstract PM buildUpdatablePredictiveModel(Iterable<? extends AbstractInstance> trainingData);
    public abstract void updatePredictiveModel(PM predictiveModel, Iterable<? extends AbstractInstance> newData);
    public abstract void stripData(PM predictiveModel);

    public PM buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        //check if we want to build a new predictive model or update existing
        if (predictiveModel == null) {
            predictiveModel = buildUpdatablePredictiveModel(trainingData);
        } else {
            updatePredictiveModel(this.predictiveModel, trainingData);
        }
        return predictiveModel;
    }
}
