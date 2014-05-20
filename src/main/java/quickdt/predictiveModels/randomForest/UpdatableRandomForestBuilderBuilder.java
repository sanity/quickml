package quickdt.predictiveModels.randomForest;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;

import java.util.Map;

public class UpdatableRandomForestBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, UpdatableRandomForestBuilder> {
    private final RandomForestBuilderBuilder randomForestBuilderBuilder;

    public UpdatableRandomForestBuilderBuilder(RandomForestBuilderBuilder randomForestBuilderBuilder) {
        this.randomForestBuilderBuilder = randomForestBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return randomForestBuilderBuilder.createDefaultParametersToOptimize();
    }

    @Override
    public UpdatableRandomForestBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        return new UpdatableRandomForestBuilder(randomForestBuilderBuilder.buildBuilder(predictiveModelParameters));
    }
}
