package quickdt.predictiveModels.randomForest;

import com.google.common.collect.Maps;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilderBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class RandomForestBuilderBuilder implements UpdatablePredictiveModelBuilderBuilder<RandomForest, RandomForestBuilder> {
    private static final String NUM_TREES = "numTrees";
    private static final String BAG_SIZE = "bagSize";
    private final TreeBuilderBuilder treeBuilderBuilder;
    public RandomForestBuilderBuilder() {
        this(new TreeBuilderBuilder());
    }

    public RandomForestBuilderBuilder(TreeBuilderBuilder treeBuilderBuilder) {
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
