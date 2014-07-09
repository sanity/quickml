package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.collect.Maps;
import quickdt.crossValidation.DateTimeExtractor;
import quickdt.crossValidation.SimpleDateFormatExtractor;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;

import java.util.Map;

public class TemporallyReweightedPMBuilderBuilder implements UpdatablePredictiveModelBuilderBuilder<TemporallyReweightedPM, TemporallyReweightedPMBuilder> {

    public static final String HALF_LIFE_OF_NEGATIVE = "halfLifeOfNegative";
    public static final String HALF_LIFE_OF_POSITIVE = "halfLifeOfPositive";
    public static final String DATE_EXTRACTOR = "dateExtractor";
    private final PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder;

    public TemporallyReweightedPMBuilderBuilder(PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderBuilder.createDefaultParametersToOptimize());
        parametersToOptimize.put(HALF_LIFE_OF_NEGATIVE, new FixedOrderRecommender(1.0, 7.0, 30.0));
        parametersToOptimize.put(HALF_LIFE_OF_POSITIVE, new FixedOrderRecommender(1.0, 7.0, 30.0));
        parametersToOptimize.put(DATE_EXTRACTOR, new FixedOrderRecommender(new SimpleDateFormatExtractor()));
        return parametersToOptimize;
    }

    @Override
    public TemporallyReweightedPMBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        final double halfLifeOfPositive = (Double) predictiveModelConfig.get(HALF_LIFE_OF_POSITIVE);
        final double halfLifeOfNegative = (Double) predictiveModelConfig.get(HALF_LIFE_OF_NEGATIVE);
        final DateTimeExtractor dateTimeExtractor = (DateTimeExtractor) predictiveModelConfig.get(DATE_EXTRACTOR);
        return new TemporallyReweightedPMBuilder(wrappedBuilderBuilder.buildBuilder(predictiveModelConfig), dateTimeExtractor)
                                                 .halfLifeOfNegative(halfLifeOfNegative)
                                                 .halfLifeOfPositive(halfLifeOfPositive);
    }
}
