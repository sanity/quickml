package quickml.supervised.classifier.randomForest;

import com.google.common.collect.Maps;
import quickml.supervised.classifier.decisionTree.TreeBuilderFactory;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.UpdatablePredictiveModelBuilderFactory;
import quickml.supervised.classifier.decisionTree.TreeBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class RandomForestBuilderFactory implements UpdatablePredictiveModelBuilderFactory<Map<String, Serializable>,RandomForest, RandomForestBuilder> {
    private static final String NUM_TREES = "numTrees";
    private static final String BAG_SIZE = "bagSize";
    private final TreeBuilderFactory treeBuilderBuilder;
    public RandomForestBuilderFactory() {
        this(new TreeBuilderFactory());
    }

    public RandomForestBuilderFactory(TreeBuilderFactory treeBuilderBuilder) {
        this.treeBuilderBuilder = treeBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(treeBuilderBuilder.createDefaultParametersToOptimize());
        parametersToOptimize.put(NUM_TREES, new FixedOrderRecommender(5, 10, 20, 40));
        parametersToOptimize.put(BAG_SIZE, new FixedOrderRecommender(0, 1000, 10000, Integer.MAX_VALUE));
        return parametersToOptimize;
    }

    @Override
    public RandomForestBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        TreeBuilder treeBuilder = treeBuilderBuilder.buildBuilder(predictiveModelParameters);
        final int numTrees = (Integer) predictiveModelParameters.get(NUM_TREES);
        final int bagSize = (Integer) predictiveModelParameters.get(BAG_SIZE);
        return new RandomForestBuilder(treeBuilder)
                .numTrees(numTrees)
                .withBagging(bagSize);

    }
}
