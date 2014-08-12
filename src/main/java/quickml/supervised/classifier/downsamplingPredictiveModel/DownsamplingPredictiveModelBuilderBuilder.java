package quickml.supervised.classifier.downsamplingPredictiveModel;

import com.google.common.collect.Maps;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilderBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilderBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 4/24/14.
 */
public class DownsamplingPredictiveModelBuilderBuilder implements UpdatablePredictiveModelBuilderBuilder<Map<String, Serializable>,DownsamplingClassifier, DownsamplingPredictiveModelBuilder> {

    private static final String MINORITY_INSTANCE_PROPORTION = "minorityInstanceProportion";
    private final PredictiveModelBuilderBuilder<Map<String, Serializable>,Classifier,?> wrappedBuilderBuilder;

    public DownsamplingPredictiveModelBuilderBuilder(PredictiveModelBuilderBuilder<Map<String, Serializable>,Classifier,?> wrappedBuilderBuilder) {
        this.wrappedBuilderBuilder = wrappedBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
               parametersToOptimize.putAll(wrappedBuilderBuilder.createDefaultParametersToOptimize());
               parametersToOptimize.put(MINORITY_INSTANCE_PROPORTION, new FixedOrderRecommender(0.1, 0.2, 0.3, 0.4, 0.5));
               return parametersToOptimize;
    }

    @Override
    public DownsamplingPredictiveModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new DownsamplingPredictiveModelBuilder(wrappedBuilderBuilder.buildBuilder(predictiveModelConfig), (Double) predictiveModelConfig.get(MINORITY_INSTANCE_PROPORTION));
    }
}
