package quickdt.predictiveModels;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by chrisreeves on 7/2/14.
 */
public class PredictiveModelWithDataBuilderBuilder<R, PM extends PredictiveModel<R, ?>, PMB extends UpdatablePredictiveModelBuilder<R, PM>> implements PredictiveModelBuilderBuilder<R, PM, PMB> {
    public static final String REBUILD_THRESHOLD = "rebuildThreshold";
    public static final String SPLIT_THRESHOLD = "splitThreshold";

    private final UpdatablePredictiveModelBuilderBuilder<R, PM, PMB> predictiveModelBuilderBuilder;

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
    public PMB buildBuilder(Map<String, Object> predictiveModelConfig) {
        PMB updatablePredictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder<R, PM> wrappedBuilder = new PredictiveModelWithDataBuilder<>(updatablePredictiveModelBuilder);
        final Integer rebuildThreshold = (Integer) predictiveModelConfig.get(REBUILD_THRESHOLD);
        final Integer splitNodeThreshold = (Integer) predictiveModelConfig.get(SPLIT_THRESHOLD);
        //find out why this cast is needed.  We know wrappedBuilder is of type PMB because PredictiveModelWithDataBuilder<R,PM> implements UpdatablePredictievModel<R,PM>...which PM extends
        return (PMB)wrappedBuilder.rebuildThreshold(rebuildThreshold).splitNodeThreshold(splitNodeThreshold);
    }
}
