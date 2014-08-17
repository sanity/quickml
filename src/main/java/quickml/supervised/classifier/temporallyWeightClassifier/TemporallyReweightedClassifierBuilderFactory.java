package quickml.supervised.classifier.temporallyWeightClassifier;

import com.google.common.collect.Maps;
import quickml.supervised.UpdatablePredictiveModelBuilderFactory;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.supervised.crossValidation.dateTimeExtractors.SimpleDateFormatExtractor;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilderFactory;

import java.io.Serializable;
import java.util.Map;

public class TemporallyReweightedClassifierBuilderFactory implements UpdatablePredictiveModelBuilderFactory<Map<String, Serializable>,TemporallyReweightedClassifier, TemporallyReweightedClassifierBuilder> {

    public static final String HALF_LIFE_OF_NEGATIVE = "halfLifeOfNegative";
    public static final String HALF_LIFE_OF_POSITIVE = "halfLifeOfPositive";
    public static final String DATE_EXTRACTOR = "dateExtractor";
    private final PredictiveModelBuilderFactory<Map<String, Serializable>,Classifier,?> wrappedBuilderBuilder;

    public TemporallyReweightedClassifierBuilderFactory(PredictiveModelBuilderFactory<Map<String, Serializable>, Classifier, ?> wrappedBuilderBuilder) {
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
    public TemporallyReweightedClassifierBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        final double halfLifeOfPositive = (Double) predictiveModelConfig.get(HALF_LIFE_OF_POSITIVE);
        final double halfLifeOfNegative = (Double) predictiveModelConfig.get(HALF_LIFE_OF_NEGATIVE);
        final DateTimeExtractor dateTimeExtractor = (DateTimeExtractor) predictiveModelConfig.get(DATE_EXTRACTOR);
        return new TemporallyReweightedClassifierBuilder(wrappedBuilderBuilder.buildBuilder(predictiveModelConfig), dateTimeExtractor)
                                                 .halfLifeOfNegative(halfLifeOfNegative)
                                                 .halfLifeOfPositive(halfLifeOfPositive);
    }
}
