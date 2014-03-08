package quickdt.predictiveModelOptimizer;

import quickdt.TreeBuilder;
import quickdt.randomForest.RandomForest;
import quickdt.randomForest.RandomForestBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class RandomForestBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, RandomForestBuilder> {

    public RandomForestBuilder build (Map<String, Object> parameters){
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth((Integer)parameters.get("maxDepth")).ignoreAttributeAtNodeProbability((Double)parameters.get("ignoreAttributeAtNodeProbability"));
        return new RandomForestBuilder(treeBuilder).numTrees((Integer)parameters.get("numTrees"));
    }
}
