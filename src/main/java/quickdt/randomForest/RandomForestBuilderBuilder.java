package quickdt.randomForest;

import com.google.common.collect.Lists;
import quickdt.PredictiveModelBuilderBuilder;
import quickdt.TreeBuilder;
import quickdt.predictiveModelOptimizer.ParameterToOptimize;
import quickdt.predictiveModelOptimizer.PropertiesBuilder;

import java.util.*;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class RandomForestBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, RandomForestBuilder> {
    Map<String, Object> initialPredictiveModelParameters;

    public RandomForestBuilderBuilder() {}

    public RandomForestBuilderBuilder(Map<String, Object> userPredictiveModelConfig) {
        this.initialPredictiveModelParameters = userPredictiveModelConfig;
    }
    @Override
    public List<ParameterToOptimize> createDefaultParametersToOptimize(){
        List<ParameterToOptimize> parametersToOptimize = Lists.<ParameterToOptimize>newArrayList();

        List<Object> depthRange = Lists.<Object>newArrayList(2, 5);
        PropertiesBuilder maxDepthPropertyBuilder = new PropertiesBuilder().setName("maxDepth").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(depthRange);
        parametersToOptimize.add(new ParameterToOptimize(maxDepthPropertyBuilder.createProperties()));

        List<Object> skipRange = Lists.<Object>newArrayList(0.6, 0.8, 0.9, 0.95, 0.98);
        PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(skipRange);
        parametersToOptimize.add(new ParameterToOptimize(ignoreAttrPropertyBuilder.createProperties()));

        List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 40);
        PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName("numTrees").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.0).setRange(numTreesRange);
        parametersToOptimize.add(new ParameterToOptimize(numTreesPropertyBuilder.createProperties()));

        List<Object> minimumScoreRange = Lists.<Object>newArrayList(Double.MIN_VALUE, 0.0, 0.00001, 0.0001, 0.001, 0.01, 0.1);
        PropertiesBuilder minimumScorePropertiesBuilder = new PropertiesBuilder().setName("minimumScore").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(false).setInitialGuessOfOptimalValue(0.001).setIsOrdinal(true).setRange(minimumScoreRange);
        parametersToOptimize.add(new ParameterToOptimize(minimumScorePropertiesBuilder.createProperties()));

        return parametersToOptimize;
    }

    @Override
    public Map<String, Object> createPredictiveModelConfig(List<ParameterToOptimize> parametersToOptimizes)  {
        Map<String, Object> predictiveModelParameters;
        if (initialPredictiveModelParameters != null)
            predictiveModelParameters = initialPredictiveModelParameters;
        else
            predictiveModelParameters = new HashMap<String, Object>();


        for (ParameterToOptimize parameterToOptimize : parametersToOptimizes)
             predictiveModelParameters.put(parameterToOptimize.properties.name, parameterToOptimize.properties.optimalValue);
        if (!predictiveModelParameters.containsKey("maxDepth"))
            predictiveModelParameters.put("maxDepth", new Integer(4));
        if (!predictiveModelParameters.containsKey("ignoreAttributeAtNodeProbability"))
            predictiveModelParameters.put("ignoreAttributeAtNodeProbability", new Double(0.7));
        if (!predictiveModelParameters.containsKey("numTrees"))
            predictiveModelParameters.put("numTrees", new Integer(4));
        if (!predictiveModelParameters.containsKey("minimumScore"))
            predictiveModelParameters.put("minimumScore", 0.0);
        return predictiveModelParameters;
    }

    @Override
    public RandomForestBuilder buildBuilder(Map<String, Object> predictiveModelParameters){
        TreeBuilder treeBuilder = new TreeBuilder()
                .maxDepth((Integer)predictiveModelParameters
                .get("maxDepth")).ignoreAttributeAtNodeProbability((Double) predictiveModelParameters.get("ignoreAttributeAtNodeProbability"))
                .minimumScore((Double) predictiveModelParameters.get("minimumScore"));
        return new RandomForestBuilder(treeBuilder).numTrees((Integer)predictiveModelParameters.get("numTrees"));

    }
}
