package quickdt.predictiveModels.randomForest;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;
import quickdt.predictiveModels.WrappedUpdatablePredictiveModelBuilder;

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
    public WrappedUpdatablePredictiveModelBuilder buildBuilder(Map predictiveModelConfig) {
        RandomForestBuilder randomForestBuilder = randomForestBuilderBuilder.buildBuilder(predictiveModelConfig);
        WrappedUpdatablePredictiveModelBuilder wrappedBuilder = new WrappedUpdatablePredictiveModelBuilder(randomForestBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
