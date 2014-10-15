package quickml.supervised.calibratedPredictiveModel;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.supervised.UpdatablePredictiveModelBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilderFactory;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.randomForest.RandomForestBuilderFactory;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;


import java.util.Map;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class CalibratedPredictiveModelBuilderFactory implements UpdatablePredictiveModelBuilderFactory<AttributesMap, CalibratedPredictiveModel, CalibratedPredictiveModelBuilder> {
    private static final String BINS_IN_CALIBRATOR = "binsInCalibrator";

    private final UpdatablePredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends UpdatablePredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderFactory;

    public CalibratedPredictiveModelBuilderFactory() {
        this(new RandomForestBuilderFactory());
    }

    public CalibratedPredictiveModelBuilderFactory(UpdatablePredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends UpdatablePredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderBuilder) {
        this.wrappedBuilderFactory = wrappedBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderFactory.createDefaultParametersToOptimize());
        parametersToOptimize.put(BINS_IN_CALIBRATOR, new FixedOrderRecommender(5, 10, 20, 40));
        return parametersToOptimize;
    }

    @Override
    public CalibratedPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new CalibratedPredictiveModelBuilder(wrappedBuilderFactory.buildBuilder(predictiveModelConfig))
                .binsInCalibrator((Integer) predictiveModelConfig.get(BINS_IN_CALIBRATOR));
    }
}


