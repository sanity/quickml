package quickdt.predictiveModels.calibratedPredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;
import quickdt.predictiveModels.WrappedUpdatablePredictiveModelBuilder;

import java.util.Map;

/**
 * Created by Chris on 5/22/2014.
 */
public class UpdatablePAVCalibratedPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder {
    private final PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder;

    public UpdatablePAVCalibratedPredictiveModelBuilderBuilder(PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder) {
        this.pavCalibratedPredictiveModelBuilderBuilder = pavCalibratedPredictiveModelBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = pavCalibratedPredictiveModelBuilderBuilder.createDefaultParametersToOptimize();
        UpdatablePredictiveModelBuilderBuilderUtils.addUpdatableParamters(map);
        return map;
    }

    @Override
    public WrappedUpdatablePredictiveModelBuilder buildBuilder(Map predictiveModelConfig) {
        PAVCalibratedPredictiveModelBuilder calibratedPredictiveModelBuilder = pavCalibratedPredictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        WrappedUpdatablePredictiveModelBuilder wrappedBuilder = new WrappedUpdatablePredictiveModelBuilder(calibratedPredictiveModelBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
