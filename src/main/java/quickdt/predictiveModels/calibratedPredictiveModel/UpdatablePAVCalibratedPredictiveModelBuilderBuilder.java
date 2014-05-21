package quickdt.predictiveModels.calibratedPredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class UpdatablePAVCalibratedPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<CalibratedPredictiveModel, UpdatablePAVCalibratedPredictiveModelBuilder> {
    private final PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder;
    private final Integer rebuildThreshold;
    private final Integer splitThreshold;

    public UpdatablePAVCalibratedPredictiveModelBuilderBuilder(PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder) {
        this(pavCalibratedPredictiveModelBuilderBuilder, null, null);
    }

    public UpdatablePAVCalibratedPredictiveModelBuilderBuilder(PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder, Integer rebuildThreshold, Integer splitThreshold) {
        this.pavCalibratedPredictiveModelBuilderBuilder = pavCalibratedPredictiveModelBuilderBuilder;
        this.rebuildThreshold = rebuildThreshold;
        this.splitThreshold = splitThreshold;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return pavCalibratedPredictiveModelBuilderBuilder.createDefaultParametersToOptimize();
    }

    @Override
    public UpdatablePAVCalibratedPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new UpdatablePAVCalibratedPredictiveModelBuilder(pavCalibratedPredictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig), rebuildThreshold, splitThreshold);
    }
}


