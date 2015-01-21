package quickml.supervised.classifier.splitOnAttribute;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;
import java.util.Set;

/**
 * Created by chrisreeves on 6/10/14.
 */
//TODO[mk] is this being used?
public class SplitOnAttributeClassifierBuilderFactory implements PredictiveModelBuilderFactory<AttributesMap,SplitOnAttributeClassifier, SplitOnAttributeClassifierBuilder> {
    private static final String MIN_AMOUNT_TOTAL_CROSS_DATA = "minAmountTotalCrossData";
    private static final String MIN_AMOUNT_CROSS_DATA_CLASSIFICATION = "minAmountCrossDataClassification";
    private static final String PERCENT_CROSS_DATA = "percentCrossData";

    private final PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>>  wrappedBuilderBuilder;
    private final String attributeKey;
    private final Set<String> attributeWhiteList;

    public SplitOnAttributeClassifierBuilderFactory(PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderBuilder, String attributeKey, Set<String> attributeWhiteList) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
        this.attributeKey = attributeKey;
        this.attributeWhiteList = attributeWhiteList;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderBuilder.createDefaultParametersToOptimize());
        parametersToOptimize.put(MIN_AMOUNT_TOTAL_CROSS_DATA, new FixedOrderRecommender(0, 100, 1000));
        parametersToOptimize.put(PERCENT_CROSS_DATA, new FixedOrderRecommender(0.1, 0.2, 0.5));
        parametersToOptimize.put(MIN_AMOUNT_CROSS_DATA_CLASSIFICATION, new FixedOrderRecommender(0, 10, 100));
        return parametersToOptimize;
    }

    @Override
    public SplitOnAttributeClassifierBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        final int minAmountCrossData = (Integer) predictiveModelConfig.get(MIN_AMOUNT_TOTAL_CROSS_DATA);
        final double percentCrossData = (Double) predictiveModelConfig.get(PERCENT_CROSS_DATA);
        final int minAmountCrossDataClassification = (Integer) predictiveModelConfig.get(MIN_AMOUNT_CROSS_DATA_CLASSIFICATION);
        return new SplitOnAttributeClassifierBuilder(attributeKey, wrappedBuilderBuilder.buildBuilder(predictiveModelConfig),
                minAmountCrossData, percentCrossData, attributeWhiteList, minAmountCrossDataClassification);
    }
}
