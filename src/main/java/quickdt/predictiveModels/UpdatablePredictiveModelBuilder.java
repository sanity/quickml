package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

import java.util.LinkedList;
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

    public PM buildPredictiveModel(final Iterable<? extends AbstractInstance> newTrainingData) {
        if (rebuildThreshold != null) {
            buildCount++;
        }
        if (this.trainingData == null) {
            //Use a copyOnWriteArrayList so when creating random forest read access is thread safe
            this.trainingData = new CopyOnWriteArrayList<>();
        }
        appendTrainingData(newTrainingData);

        //check if we want to build a new predictive model or update existing
        if (predictiveModel == null || (rebuildThreshold != null && buildCount > rebuildThreshold)) {
            buildCount = 1;
            predictiveModel = buildUpdatablePredictiveModel(this.trainingData);
        } else {
            updatePredictiveModel(this.predictiveModel, newTrainingData);
        }

        return predictiveModel;
    }

    private void appendTrainingData(Iterable<? extends AbstractInstance> newTrainingData) {
        int index = trainingData.size();
        List<AbstractInstance> dataCollection = new LinkedList<>();
        for(AbstractInstance data : newTrainingData) {
            data.index = index;
            index++;
            dataCollection.add(data);
        }
        //writing is expensive, do it all at once
        this.trainingData.addAll(dataCollection);
    }
}
