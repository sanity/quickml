package quickdt.predictiveModels.randomForest;

import com.google.common.collect.Lists;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModelOptimizer.ParameterToOptimize;
import quickdt.predictiveModelOptimizer.PropertiesBuilder;

import java.util.*;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class RandomForestBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, RandomForestBuilder> {
    private static final String MAX_DEPTH = "maxDepth";
    private static final String IGNORE_ATTRIBUTE_AT_NODE_PROBABILITY = "ignoreAttributeAtNodeProbability";
    private static final String NUM_TREES = "numTrees";
    private static final String MINIMUM_SCORE = "minimumScore";
    private static final String EXECUTOR_THREAD_COUNT = "executorThreadCount";
    private static final String BAG_SIZE = "bagSize";
    Map<String, Object> initialPredictiveModelParameters;

    public RandomForestBuilderBuilder() {
    }

    public RandomForestBuilderBuilder(Map<String, Object> userPredictiveModelConfig) {
        this.initialPredictiveModelParameters = userPredictiveModelConfig;
    }

    @Override
    public List<ParameterToOptimize> createDefaultParametersToOptimize() {
        List<ParameterToOptimize> parametersToOptimize = Lists.<ParameterToOptimize>newArrayList();
        {
            List<Object> depthRange = Lists.<Object>newArrayList(2, 5);
            PropertiesBuilder maxDepthPropertyBuilder = new PropertiesBuilder().setName(MAX_DEPTH).setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                    .setParameterTolerance(0).setErrorTolerance(.05).setRange(depthRange);
            parametersToOptimize.add(new ParameterToOptimize(maxDepthPropertyBuilder.createProperties()));
        }
        {
            List<Object> skipRange = Lists.<Object>newArrayList(0.01, 0.1, 0.3, 0.5, 0.6, 0.8, 0.9, 0.95, 0.98);
            PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName(IGNORE_ATTRIBUTE_AT_NODE_PROBABILITY).setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                    .setParameterTolerance(0).setErrorTolerance(.05).setRange(skipRange);
            parametersToOptimize.add(new ParameterToOptimize(ignoreAttrPropertyBuilder.createProperties()));
        }
        {
            List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 40);
            PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName(NUM_TREES).setBinarySearchTheRange(false).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.0).setRange(numTreesRange);
            parametersToOptimize.add(new ParameterToOptimize(numTreesPropertyBuilder.createProperties()));
        }
        {
            List<Object> minimumScoreRange = Lists.<Object>newArrayList(Double.MIN_VALUE, 0.0, 0.00001, 0.0001, 0.001, 0.01, 0.1);
            PropertiesBuilder minimumScorePropertiesBuilder = new PropertiesBuilder().setName(MINIMUM_SCORE).setBinarySearchTheRange(false).setIsMonotonicallyConvergent(false).setInitialGuessOfOptimalValue(0.001).setIsOrdinal(true).setRange(minimumScoreRange);
            parametersToOptimize.add(new ParameterToOptimize(minimumScorePropertiesBuilder.createProperties()));
        }
        {
            List<Object> bagSizeRange = Lists.<Object>newArrayList(0, 1000, 10000, Integer.MAX_VALUE);
            PropertiesBuilder bagSizePropertiesBuilder = new PropertiesBuilder().setName(BAG_SIZE).setBinarySearchTheRange(false).setIsMonotonicallyConvergent(false).setInitialGuessOfOptimalValue(Integer.MAX_VALUE).setIsOrdinal(true).setRange(bagSizeRange);
            parametersToOptimize.add(new ParameterToOptimize(bagSizePropertiesBuilder.createProperties()));
        }
        return parametersToOptimize;
    }

    @Override
    public Map<String, Object> createPredictiveModelConfig(List<ParameterToOptimize> parametersToOptimizes) {
        Map<String, Object> predictiveModelParameters;
        if (initialPredictiveModelParameters != null)
            predictiveModelParameters = initialPredictiveModelParameters;
        else
            predictiveModelParameters = new HashMap<String, Object>();


        for (ParameterToOptimize parameterToOptimize : parametersToOptimizes)
            predictiveModelParameters.put(parameterToOptimize.properties.name, parameterToOptimize.properties.optimalValue);
        if (!predictiveModelParameters.containsKey(MAX_DEPTH))
            predictiveModelParameters.put(MAX_DEPTH, new Integer(4));
        if (!predictiveModelParameters.containsKey(IGNORE_ATTRIBUTE_AT_NODE_PROBABILITY))
            predictiveModelParameters.put(IGNORE_ATTRIBUTE_AT_NODE_PROBABILITY, new Double(0.7));
        if (!predictiveModelParameters.containsKey(NUM_TREES))
            predictiveModelParameters.put(NUM_TREES, new Integer(4));
        if (!predictiveModelParameters.containsKey(MINIMUM_SCORE))
            predictiveModelParameters.put(MINIMUM_SCORE, 0.0);
        if (!predictiveModelParameters.containsKey(EXECUTOR_THREAD_COUNT))
            predictiveModelParameters.put(EXECUTOR_THREAD_COUNT, 8);
        if (!predictiveModelParameters.containsKey(BAG_SIZE))
            predictiveModelParameters.put(BAG_SIZE, Integer.MAX_VALUE);
        return predictiveModelParameters;
    }

    @Override
    public RandomForestBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        TreeBuilder treeBuilder = new TreeBuilder()
                .maxDepth((Integer) predictiveModelParameters
                        .get(MAX_DEPTH)).ignoreAttributeAtNodeProbability((Double) predictiveModelParameters.get(IGNORE_ATTRIBUTE_AT_NODE_PROBABILITY))
                .minimumScore((Double) predictiveModelParameters.get(MINIMUM_SCORE));
        return new RandomForestBuilder(treeBuilder)
                .numTrees((Integer) predictiveModelParameters.get(NUM_TREES))
                .executorThreadCount((Integer) predictiveModelParameters.get(EXECUTOR_THREAD_COUNT))
                .withBagging((Integer) predictiveModelParameters.get(BAG_SIZE));

    }
}
