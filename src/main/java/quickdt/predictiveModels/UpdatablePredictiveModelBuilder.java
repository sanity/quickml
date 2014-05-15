package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class UpdatablePredictiveModelBuilder<PM extends PredictiveModel> implements PredictiveModelBuilder<PM> {
    protected List<AbstractInstance> trainingData;
    protected PM predictiveModel;
    protected Integer rebuildThreshold = null;
    protected Integer buildCount = 0;

    public UpdatablePredictiveModelBuilder(Integer rebuildThreshold) {
        this.rebuildThreshold = rebuildThreshold;
    }
    public abstract PM buildUpdatablePredictiveModel(Iterable<? extends AbstractInstance> trainingData);
    public abstract void updatePredictiveModel(PM predictiveModel, Iterable<? extends AbstractInstance> newData);

    public PM buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        if (rebuildThreshold != null) {
            buildCount++;
        }
        if (this.trainingData == null) {
            this.trainingData = new CopyOnWriteArrayList<>();
        }
        appendTrainingData(trainingData);

        //check if we want to build a new predictive model or update existing
        if (predictiveModel == null || (rebuildThreshold != null && buildCount > rebuildThreshold)) {
            buildCount = 1;
            predictiveModel = buildUpdatablePredictiveModel(this.trainingData);
        } else {
            updatePredictiveModel(this.predictiveModel, trainingData);
        }

        return predictiveModel;
    }

    private void appendTrainingData(Iterable<? extends AbstractInstance> newData) {
        int index = trainingData.size();
        for(AbstractInstance data : newData) {
            data.index = index;
            this.trainingData.add(data);
            index++;
        }
    }
}
