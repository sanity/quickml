package quickdt.predictiveModels.randomForest;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.util.List;


/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatableRandomForestBuilder extends UpdatablePredictiveModelBuilder<RandomForest> {
    private final RandomForestBuilder randomForestBuilder;

    public UpdatableRandomForestBuilder(RandomForestBuilder randomForestBuilder) {
        this(randomForestBuilder, null, null, null);
    }

    public UpdatableRandomForestBuilder(RandomForestBuilder randomForestBuilder, Integer rebuildThreshold, Integer splitThreshold) {
        this(randomForestBuilder, null, rebuildThreshold, splitThreshold);
    }

    public UpdatableRandomForestBuilder(RandomForestBuilder randomForestBuilder, RandomForest randomForest, Integer rebuildThreshold, Integer splitThreshold) {
        super(randomForest, rebuildThreshold, splitThreshold);
        this.randomForestBuilder = randomForestBuilder.updatable(true);
    }

    @Override
    public RandomForest buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(RandomForest predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        randomForestBuilder.updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
    }

    @Override
    public void stripData(RandomForest predictiveModel) {
        randomForestBuilder.stripData(predictiveModel);
    }

}
