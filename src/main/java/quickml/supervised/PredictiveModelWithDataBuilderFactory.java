package quickml.supervised;

import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by chrisreeves on 7/2/14.
 */
public class PredictiveModelWithDataBuilderFactory<R, PM extends PredictiveModel<R, ?>> implements PredictiveModelBuilderFactory<R, PM, PredictiveModelWithDataBuilder<R, PM>> {
    public static final String REBUILD_THRESHOLD = "rebuildThreshold";
    public static final String SPLIT_THRESHOLD = "splitThreshold";

    private final UpdatablePredictiveModelBuilderFactory<R, PM, PredictiveModelWithDataBuilder<R, PM>> predictiveModelBuilderBuilder;

    public PredictiveModelWithDataBuilderFactory(UpdatablePredictiveModelBuilderFactory predictiveModelBuilderBuilder) {
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
