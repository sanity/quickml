package quickdt.predictiveModels;


import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by Chris on 5/21/2014.
 */
public abstract class UpdatablePredictiveModelBuilderBuilder {

    public static final String REBUILD_THRESHOLD = "rebuildThreshold";
    public static final String SPLIT_THRESHOLD = "splitThreshold";

    public Map<String, FieldValueRecommender> addUpdatableParamters(Map<String, FieldValueRecommender> map) {
        map.put(REBUILD_THRESHOLD, new FixedOrderRecommender(0, 5));
        map.put(SPLIT_THRESHOLD, new FixedOrderRecommender(0, 1, 5));
        return map;
    }

    public void applyUpdatableConfig(UpdatablePredictiveModelBuilder predictiveModelBuilder, final Map<String, Object> predictiveModelConfig) {
        final Integer rebuildThreshold = (Integer) predictiveModelConfig.get(REBUILD_THRESHOLD);
        final Integer splitNodeThreshold = (Integer) predictiveModelConfig.get(SPLIT_THRESHOLD);
        predictiveModelBuilder.rebuildThreshold(rebuildThreshold).splitNodeThreshold(splitNodeThreshold);
    }
}
