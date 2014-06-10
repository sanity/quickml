package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import com.google.common.collect.Maps;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import java.util.Map;

/**
 * Created by chrisreeves on 6/10/14.
 */
public class SplitOnAttributePMBuilderBuilder implements PredictiveModelBuilderBuilder<SplitOnAttributePM, SplitOnAttributePMBuilder> {

    private final PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder;
    private final String attributeKey;

    public SplitOnAttributePMBuilderBuilder(PredictiveModelBuilderBuilder<?, ?> wrappedBuilderBuilder, String attributeKey) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
        this.attributeKey = attributeKey;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderBuilder.createDefaultParametersToOptimize());
        return parametersToOptimize;
    }

    @Override
    public SplitOnAttributePMBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new SplitOnAttributePMBuilder(attributeKey, wrappedBuilderBuilder.buildBuilder(predictiveModelConfig));
    }
}
