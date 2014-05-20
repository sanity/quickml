package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.collect.Maps;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilderBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class UpdatablePAVCalibratedPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<CalibratedPredictiveModel, UpdatablePAVCalibratedPredictiveModelBuilder> {
    private final PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder;

    public UpdatablePAVCalibratedPredictiveModelBuilderBuilder(PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder) {
        this.pavCalibratedPredictiveModelBuilderBuilder = pavCalibratedPredictiveModelBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return pavCalibratedPredictiveModelBuilderBuilder.createDefaultParametersToOptimize();
    }

    @Override
    public UpdatablePAVCalibratedPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new UpdatablePAVCalibratedPredictiveModelBuilder(pavCalibratedPredictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig));
    }
}


