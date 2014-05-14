package quickdt.predictiveModels.randomForest;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;


/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatableRandomForestBuilder extends UpdatablePredictiveModelBuilder<RandomForest> {
    private final RandomForestBuilder randomForestBuilder;

    public UpdatableRandomForestBuilder(RandomForestBuilder randomForestBuilder) {
        this(randomForestBuilder, null);
    }

    public UpdatableRandomForestBuilder(RandomForestBuilder randomForestBuilder, Integer rebuildThreshold) {
        super(rebuildThreshold);
        this.randomForestBuilder = randomForestBuilder;
    }

    @Override
    public RandomForest buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(RandomForest predictiveModel, Iterable<? extends AbstractInstance> trainingData) {
        randomForestBuilder.updatePredictiveModel(predictiveModel, trainingData);
    }

}
