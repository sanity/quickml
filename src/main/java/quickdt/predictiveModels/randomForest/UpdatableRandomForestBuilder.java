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

    public UpdatableRandomForestBuilder(RandomForestBuilder randomForestBuilder, RandomForest randomForest) {
        super(randomForest);
        this.randomForestBuilder = randomForestBuilder.updatable(true);
    }

    @Override
    public RandomForest buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(RandomForest predictiveModel, Iterable<? extends AbstractInstance> newData) {
        randomForestBuilder.updatePredictiveModel(predictiveModel, newData);
    }

    @Override
    public void stripData(RandomForest predictiveModel) {
        randomForestBuilder.stripData(predictiveModel);
    }

}
