package quickdt.predictiveModels.randomForest;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;

import java.util.Map;

/**
 * Created by Chris on 5/22/2014.
 */
public class UpdatableRandomForestBuilderBuilder implements PredictiveModelBuilderBuilder {
    private final RandomForestBuilderBuilder randomForestBuilderBuilder;

    public UpdatableRandomForestBuilderBuilder(RandomForestBuilderBuilder randomForestBuilderBuilder) {
        this.randomForestBuilderBuilder = randomForestBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = randomForestBuilderBuilder.createDefaultParametersToOptimize();
        UpdatablePredictiveModelBuilderBuilderUtils.addUpdatableParamters(map);
        return map;
    }

    @Override
    public PredictiveModelWithDataBuilder buildBuilder(Map predictiveModelConfig) {
        RandomForestBuilder randomForestBuilder = randomForestBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder wrappedBuilder = new PredictiveModelWithDataBuilder(randomForestBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
