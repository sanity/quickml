package quickdt.predictiveModels.decisionTree;

import com.google.common.collect.Maps;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;
import quickdt.predictiveModels.decisionTree.scorers.GiniImpurityScorer;
import quickdt.predictiveModels.decisionTree.scorers.InformationGainScorer;
import quickdt.predictiveModels.decisionTree.scorers.MSEScorer;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.util.Map;

/**
 * Created by ian on 4/12/14.
 */
public class TreeBuilderBuilder implements UpdatablePredictiveModelBuilderBuilder<Tree, TreeBuilder> {

    private static final String IGNORE_ATTR_PROB = "ignoreAttrProb";
    private static final String MAX_DEPTH = "maxDepth";
    private static final String MIN_SCORE = "minScore";
    private static final String MIN_CAT_ATTR_OCC = "minCatAttrOcc";
    private static final String MIN_LEAF_INSTANCES = "minLeafInstances";
    private static final String SCORER= "scorer";

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.5, 0.0, 0.1, 0.2, 0.4, 0.7, 0.8, 0.9, 0.95, 0.98, 0.99));
        parametersToOptimize.put(MAX_DEPTH, new FixedOrderRecommender(Integer.MAX_VALUE, 2, 3, 4, 5, 6, 7, 9));
        parametersToOptimize.put(MIN_SCORE, new FixedOrderRecommender(0.00000000000001, Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        parametersToOptimize.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(5, 0, 1, 64, 1024, 4098));
        parametersToOptimize.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 10, 100, 1000, 10000, 100000));
        parametersToOptimize.put(SCORER, new FixedOrderRecommender(new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE), new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE), new SplitDiffScorer(), new InformationGainScorer(), new GiniImpurityScorer()));
        return parametersToOptimize;
    }

    @Override
    public TreeBuilder buildBuilder(final Map<String, Object> cfg) throws NullPointerException {
        return new TreeBuilder((Scorer)cfg.get(SCORER))
                .ignoreAttributeAtNodeProbability((Double) cfg.get(IGNORE_ATTR_PROB))
                .maxDepth((Integer) cfg.get(MAX_DEPTH))
                .minimumScore((Double) cfg.get(MIN_SCORE))
                .minCategoricalAttributeValueOccurances((Integer) cfg.get(MIN_CAT_ATTR_OCC))
                .minLeafInstances((Integer) cfg.get(MIN_LEAF_INSTANCES))
                ;
    }
}
