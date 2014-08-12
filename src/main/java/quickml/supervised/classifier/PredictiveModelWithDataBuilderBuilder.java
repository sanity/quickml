package quickml.supervised.classifier;

import quickml.predictiveModelOptimizer.FieldValueRecommender;
import quickml.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by chrisreeves on 7/2/14.
 */
public class PredictiveModelWithDataBuilderBuilder<R, PM extends PredictiveModel<R, ?>> implements PredictiveModelBuilderBuilder<R, PM, PredictiveModelWithDataBuilder<R, PM>> {
    public static final String REBUILD_THRESHOLD = "rebuildThreshold";
    public static final String SPLIT_THRESHOLD = "splitThreshold";

    private final UpdatablePredictiveModelBuilderBuilder<R, PM, PredictiveModelWithDataBuilder<R, PM>> predictiveModelBuilderBuilder;

    public PredictiveModelWithDataBuilderBuilder(UpdatablePredictiveModelBuilderBuilder predictiveModelBuilderBuilder) {
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = predictiveModelBuilderBuilder.createDefaultParametersToOptimize();
        map.put(REBUILD_THRESHOLD, new FixedOrderRecommender(0, 25));
        map.put(SPLIT_THRESHOLD, new FixedOrderRecommender(0, 5));
        return map;
    }

    //PMB
    @Override
    public PredictiveModelWithDataBuilder<R, PM> buildBuilder(Map<String, Object> predictiveModelConfig) {
        UpdatablePredictiveModelBuilder<R, PM> updatablePredictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder<R, PM> wrappedBuilder = new PredictiveModelWithDataBuilder<>(updatablePredictiveModelBuilder);
        final Integer rebuildThreshold = (Integer) predictiveModelConfig.get(REBUILD_THRESHOLD);
        final Integer splitNodeThreshold = (Integer) predictiveModelConfig.get(SPLIT_THRESHOLD);
        return wrappedBuilder.rebuildThreshold(rebuildThreshold).splitNodeThreshold(splitNodeThreshold);
    }
}
