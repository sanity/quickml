package quickdt.predictiveModels.calibratedPredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class UpdatablePAVCalibratedPredictiveModelBuilderBuilder extends UpdatablePredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<CalibratedPredictiveModel, UpdatablePAVCalibratedPredictiveModelBuilder> {
    private final PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder;

    public UpdatablePAVCalibratedPredictiveModelBuilderBuilder(PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder) {
        this.pavCalibratedPredictiveModelBuilderBuilder = pavCalibratedPredictiveModelBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return addUpdatableParamters(pavCalibratedPredictiveModelBuilderBuilder.createDefaultParametersToOptimize());
    }

    @Override
    public UpdatablePAVCalibratedPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        final PAVCalibratedPredictiveModelBuilder builder = pavCalibratedPredictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        UpdatablePAVCalibratedPredictiveModelBuilder updatableBuilder = new UpdatablePAVCalibratedPredictiveModelBuilder(builder, null);
        applyUpdatableConfig(updatableBuilder, predictiveModelConfig);
        return updatableBuilder;
    }
}


