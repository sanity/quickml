package quickdt.predictiveModels.downsamplingPredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;

import java.util.Map;

public class UpdatableDownsamplingPredictiveModelBuilderBuilder extends UpdatablePredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<DownsamplingPredictiveModel, UpdatableDownsamplingPredictiveModelBuilder> {

    private final DownsamplingPredictiveModelBuilderBuilder downsamplingPredictiveModelBuilderBuilder;

    public UpdatableDownsamplingPredictiveModelBuilderBuilder(DownsamplingPredictiveModelBuilderBuilder downsamplingPredictiveModelBuilderBuilder1) {
        this.downsamplingPredictiveModelBuilderBuilder = downsamplingPredictiveModelBuilderBuilder1;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return addUpdatableParamters(downsamplingPredictiveModelBuilderBuilder.createDefaultParametersToOptimize());
    }

    @Override
    public UpdatableDownsamplingPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        final DownsamplingPredictiveModelBuilder builder = downsamplingPredictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        UpdatableDownsamplingPredictiveModelBuilder updatableBuilder = new UpdatableDownsamplingPredictiveModelBuilder(builder, null);
        applyUpdatableConfig(updatableBuilder, predictiveModelConfig);
        return updatableBuilder;
    }
}
