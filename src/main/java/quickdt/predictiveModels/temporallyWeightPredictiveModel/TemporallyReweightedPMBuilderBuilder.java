package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.collect.Maps;
import quickdt.crossValidation.SimpleDateFormatExtractor;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import java.util.Map;



public class TemporallyReweightedPMBuilderBuilder implements PredictiveModelBuilderBuilder<TemporallyReweightedPM, TemporallyReweightedPMBuilder> {

    private final PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder;

    public TemporallyReweightedPMBuilderBuilder(PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderBuilder.createDefaultParametersToOptimize());
        parametersToOptimize.put("halfLifeOfNegative", new FixedOrderRecommender(0, 100, 1000));
        parametersToOptimize.put("halfLifeOfPositive", new FixedOrderRecommender(0, 100, 1000));
        return parametersToOptimize;
    }

    @Override //set date time extractor to be correc
    public TemporallyReweightedPMBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        final double halfLifeOfPositive = (Double) predictiveModelConfig.get("halfLifeOfPositive");
        final double halfLifeOfNegative = (Double) predictiveModelConfig.get("halfLifeOfNegative");
        return new TemporallyReweightedPMBuilder(wrappedBuilderBuilder.buildBuilder(predictiveModelConfig), new SimpleDateFormatExtractor())
                                                 .halfLifeOfNegative(halfLifeOfNegative)
                                                 .halfLifeOfPositive(halfLifeOfPositive);
    }
}
