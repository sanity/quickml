package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.collect.Maps;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilderBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class PAVCalibratedPredictiveModelBuilderBuilder implements UpdatablePredictiveModelBuilderBuilder<CalibratedPredictiveModel, PAVCalibratedPredictiveModelBuilder> {
    private static final String BINS_IN_CALIBRATOR = "binsInCalibrator";

    private final PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder;

    public PAVCalibratedPredictiveModelBuilderBuilder() {
        this(new RandomForestBuilderBuilder());
    }

    public PAVCalibratedPredictiveModelBuilderBuilder(PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderBuilder.createDefaultParametersToOptimize());
        parametersToOptimize.put(BINS_IN_CALIBRATOR, new FixedOrderRecommender(5, 10, 20, 40));
        return parametersToOptimize;
    }

    @Override
    public PAVCalibratedPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new PAVCalibratedPredictiveModelBuilder(wrappedBuilderBuilder.buildBuilder(predictiveModelConfig))
                .binsInCalibrator((Integer) predictiveModelConfig.get(BINS_IN_CALIBRATOR));
    }
}


