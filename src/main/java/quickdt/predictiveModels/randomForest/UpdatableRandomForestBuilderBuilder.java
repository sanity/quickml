package quickdt.predictiveModels.randomForest;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;

import java.util.Map;

public class UpdatableRandomForestBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, UpdatableRandomForestBuilder> {
    private final Integer rebuildThreshold;
    private final Integer splitThreshold;
    private final RandomForestBuilderBuilder randomForestBuilderBuilder;

    public UpdatableRandomForestBuilderBuilder(RandomForestBuilderBuilder randomForestBuilderBuilder) {
        this(randomForestBuilderBuilder, null, null);
    }

    public UpdatableRandomForestBuilderBuilder(RandomForestBuilderBuilder randomForestBuilderBuilder, Integer rebuildThreshold, Integer splitThreshold) {
        this.randomForestBuilderBuilder = randomForestBuilderBuilder;
        this.rebuildThreshold = rebuildThreshold;
        this.splitThreshold = splitThreshold;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return randomForestBuilderBuilder.createDefaultParametersToOptimize();
    }

    @Override
    public UpdatableRandomForestBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        return new UpdatableRandomForestBuilder(randomForestBuilderBuilder.buildBuilder(predictiveModelParameters), rebuildThreshold, splitThreshold);
    }
}
