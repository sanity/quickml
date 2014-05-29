package quickdt.predictiveModels.downsamplingPredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;

import java.util.Map;

/**
 * Created by Chris on 5/22/2014.
 */
public class UpdatableDownsamplingPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder {
    private final DownsamplingPredictiveModelBuilderBuilder downsamplingPredictiveModelBuilderBuilder;

    public UpdatableDownsamplingPredictiveModelBuilderBuilder(DownsamplingPredictiveModelBuilderBuilder downsamplingPredictiveModelBuilderBuilder) {
        this.downsamplingPredictiveModelBuilderBuilder = downsamplingPredictiveModelBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = downsamplingPredictiveModelBuilderBuilder.createDefaultParametersToOptimize();
        UpdatablePredictiveModelBuilderBuilderUtils.addUpdatableParamters(map);
        return map;
    }

    @Override
    public PredictiveModelWithDataBuilder buildBuilder(Map predictiveModelConfig) {
        DownsamplingPredictiveModelBuilder downsamplingPredictiveModelBuilder = downsamplingPredictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder wrappedBuilder = new PredictiveModelWithDataBuilder(downsamplingPredictiveModelBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
