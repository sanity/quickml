package quickdt.predictiveModels.featureEngineering;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by ian on 5/21/14.
 */
public class FeatureEngineeringPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<FeatureEngineeredPredictiveModel, FeatureEngineeringPredictiveModelBuilder> {

    private final PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder;
    private final List<? extends AttributesEnrichStrategy> enrichStrategies;

    public FeatureEngineeringPredictiveModelBuilderBuilder(
            PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder,
            List<? extends AttributesEnrichStrategy> enrichStrategies) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
        this.enrichStrategies = enrichStrategies;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        // Currently we don't have any parameters for this builderBuilder
        return wrappedBuilderBuilder.createDefaultParametersToOptimize();
    }

    @Override
    public FeatureEngineeringPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new FeatureEngineeringPredictiveModelBuilder(wrappedBuilderBuilder.buildBuilder(predictiveModelConfig), enrichStrategies);
    }
}
