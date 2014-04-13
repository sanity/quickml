package quickdt.predictiveModels.decisionTree;

import com.google.common.collect.Maps;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;

import java.util.Map;

/**
 * Created by ian on 4/12/14.
 */
public class TreeBuilderBuilder implements PredictiveModelBuilderBuilder<Tree, TreeBuilder> {

    private static final String IGNORE_ATTR_PROB = "ignoreAttrProb";
    private static final String MAX_DEPTH = "maxDepth";
    private static final String MIN_SCORE = "minScore";
    private static final String MIN_CAT_ATTR_OCC = "minCatAttrOcc";

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.1, 0.2, 0.4, 0.5, 0.7, 0.8, 0.9, 0.95, 0.98, 0.99));
        parametersToOptimize.put(MAX_DEPTH, new FixedOrderRecommender(2, 3, 4, 5, 6, 7, 9));
        parametersToOptimize.put(MIN_SCORE, new FixedOrderRecommender(-Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        parametersToOptimize.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(0, 1, 4, 64, 1024, 4098));
        return parametersToOptimize;
    }

    @Override
    public TreeBuilder buildBuilder(final Map<String, Object> cfg) throws NullPointerException {
        return new TreeBuilder()
                .ignoreAttributeAtNodeProbability((Double) cfg.get(IGNORE_ATTR_PROB))
                .maxDepth((Integer) cfg.get(MAX_DEPTH))
                .minimumScore((Double) cfg.get(MIN_SCORE))
                .minCategoricalAttributeValueOccurances((Integer) cfg.get(MIN_CAT_ATTR_OCC))
                ;
    }
}
