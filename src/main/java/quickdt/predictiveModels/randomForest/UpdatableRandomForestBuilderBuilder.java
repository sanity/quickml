package quickdt.predictiveModels.randomForest;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;

import java.util.Map;

public class UpdatableRandomForestBuilderBuilder extends UpdatablePredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, UpdatableRandomForestBuilder> {
    private final RandomForestBuilderBuilder randomForestBuilderBuilder;

    public UpdatableRandomForestBuilderBuilder() {
        this(new RandomForestBuilderBuilder());
    }

    public UpdatableRandomForestBuilderBuilder(RandomForestBuilderBuilder randomForestBuilderBuilder) {
        this.randomForestBuilderBuilder = randomForestBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return addUpdatableParamters(randomForestBuilderBuilder.createDefaultParametersToOptimize());
    }

    @Override
    public UpdatableRandomForestBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        RandomForestBuilder randomForestBuilder = randomForestBuilderBuilder.buildBuilder(predictiveModelParameters);
        UpdatableRandomForestBuilder updatableRandomForestBuilder = new UpdatableRandomForestBuilder(randomForestBuilder, null);
        applyUpdatableConfig(updatableRandomForestBuilder, predictiveModelParameters);
        return updatableRandomForestBuilder;
    }
}
