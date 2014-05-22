package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PredictiveModelBuilder that supports adding data to predictive models
 * The builder will keep track of the number of times the model has been built, and if the rebuild threshold is passed the model will be rebuilt
 * If the split node threshold is passed the leaves will be rebuilt
 */
public abstract class UpdatablePredictiveModelBuilder<PM extends PredictiveModel> implements PredictiveModelBuilder<PM> {
    protected List<AbstractInstance> trainingData;
    protected PM predictiveModel;
    protected Integer rebuildThreshold;
    protected Integer splitNodeThreshold;
    protected int buildCount = 0;

    public UpdatablePredictiveModelBuilder(PM predictiveModel) {
        this.predictiveModel = predictiveModel;
    }

    public UpdatablePredictiveModelBuilder rebuildThreshold(Integer rebuildThreshold) {
        this.rebuildThreshold = rebuildThreshold;
        return this;
    }

    public UpdatablePredictiveModelBuilder splitNodeThreshold(Integer splitNodeThreshold) {
        this.splitNodeThreshold = splitNodeThreshold;
        return this;
    }

    public PredictiveModelBuilder updatable(boolean updatable) {
        return this;
    }

    public abstract PM buildUpdatablePredictiveModel(Iterable<? extends AbstractInstance> trainingData);

    public PM buildPredictiveModel(Iterable<? extends AbstractInstance> newData) {
        if (rebuildThreshold != null || splitNodeThreshold != null) {
            buildCount++;
        }

        if (trainingData == null) {
            trainingData = new CopyOnWriteArrayList<>();
        }
        appendTrainingData(newData);

        //check if we want to build a new predictive model or update existing
        if (predictiveModel == null || (rebuildThreshold != null && rebuildThreshold != 0 && buildCount > rebuildThreshold)) {
            buildCount = 1;
            predictiveModel = buildUpdatablePredictiveModel(trainingData);
        } else {
            boolean splitNodes = splitNodeThreshold != null && splitNodeThreshold != 0 && buildCount % splitNodeThreshold == 0;
            updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
        }

        return predictiveModel;
    }

    private void appendTrainingData(Iterable<? extends AbstractInstance> newTrainingData) {
        int index = trainingData.size();
        List<AbstractInstance> dataList = new ArrayList<>();
        for(AbstractInstance data : newTrainingData) {
            data.index = index;
            index++;
            dataList.add(data);
        }
        //writing is expensive, do it all at once
        trainingData.addAll(dataList);
    }
}
