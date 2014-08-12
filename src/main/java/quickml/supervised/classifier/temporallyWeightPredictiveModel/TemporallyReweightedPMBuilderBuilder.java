package quickml.supervised.classifier.temporallyWeightPredictiveModel;

import com.google.common.collect.Maps;
import quickml.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.crossValidation.dateTimeExtractors.SimpleDateFormatExtractor;
import quickml.predictiveModelOptimizer.FieldValueRecommender;
import quickml.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.PredictiveModelBuilderBuilder;
import quickml.supervised.classifier.UpdatablePredictiveModelBuilderBuilder;

import java.io.Serializable;
import java.util.Map;

public class TemporallyReweightedPMBuilderBuilder implements UpdatablePredictiveModelBuilderBuilder<Map<String, Serializable>,TemporallyReweightedPM, TemporallyReweightedPMBuilder> {

    public static final String HALF_LIFE_OF_NEGATIVE = "halfLifeOfNegative";
    public static final String HALF_LIFE_OF_POSITIVE = "halfLifeOfPositive";
    public static final String DATE_EXTRACTOR = "dateExtractor";
    private final PredictiveModelBuilderBuilder<Map<String, Serializable>,Classifier,?> wrappedBuilderBuilder;

    public TemporallyReweightedPMBuilderBuilder(PredictiveModelBuilderBuilder<Map<String, Serializable>,Classifier,?> wrappedBuilderBuilder) {
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
