package quickdt.predictiveModels;


import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by Chris on 5/21/2014.
 */
public class UpdatablePredictiveModelBuilderBuilderUtils {

    public static final String REBUILD_THRESHOLD = "rebuildThreshold";
    public static final String SPLIT_THRESHOLD = "splitThreshold";

    public static Map<String, FieldValueRecommender> addUpdatableParamters(Map<String, FieldValueRecommender> map) {
        map.put(REBUILD_THRESHOLD, new FixedOrderRecommender(0, 25));
        map.put(SPLIT_THRESHOLD, new FixedOrderRecommender(0, 5));
        return map;
    }

    public static void applyUpdatableConfig(PredictiveModelWithDataBuilder predictiveModelBuilder, final Map<String, Object> predictiveModelConfig) {
        final Integer rebuildThreshold = (Integer) predictiveModelConfig.get(REBUILD_THRESHOLD);
        final Integer splitNodeThreshold = (Integer) predictiveModelConfig.get(SPLIT_THRESHOLD);
        predictiveModelBuilder.rebuildThreshold(rebuildThreshold).splitNodeThreshold(splitNodeThreshold);
    }
}
