package quickdt.predictiveModels;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by chrisreeves on 7/2/14.
 */
public class PredictiveModelWithDataBuilderBuilder implements PredictiveModelBuilderBuilder<PredictiveModel, PredictiveModelWithDataBuilder<PredictiveModel>>{
    public static final String REBUILD_THRESHOLD = "rebuildThreshold";
    public static final String SPLIT_THRESHOLD = "splitThreshold";

    private final UpdatablePredictiveModelBuilderBuilder predictiveModelBuilderBuilder;

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

    @Override
    public PredictiveModelWithDataBuilder<PredictiveModel> buildBuilder(Map<String, Object> predictiveModelConfig) {
        UpdatablePredictiveModelBuilder updatablePredictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder wrappedBuilder = new PredictiveModelWithDataBuilder(updatablePredictiveModelBuilder);
        final Integer rebuildThreshold = (Integer) predictiveModelConfig.get(REBUILD_THRESHOLD);
        final Integer splitNodeThreshold = (Integer) predictiveModelConfig.get(SPLIT_THRESHOLD);
        return wrappedBuilder.rebuildThreshold(rebuildThreshold).splitNodeThreshold(splitNodeThreshold);
    }
}
