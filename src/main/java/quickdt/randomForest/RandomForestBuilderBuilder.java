package quickdt.randomForest;

import com.google.common.collect.Lists;
import quickdt.PredictiveModelBuilderBuilder;
import quickdt.TreeBuilder;
import quickdt.predictiveModelOptimizer.Parameter;
import quickdt.predictiveModelOptimizer.PropertiesBuilder;

import java.util.*;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class RandomForestBuilderBuilder implements PredictiveModelBuilderBuilder<RandomForest, RandomForestBuilder> {

    @Override
    public List<Parameter> createDefaultParameters(){
        List<Parameter> parameters = Lists.<Parameter>newArrayList();

        List<Object> depthRange = Lists.<Object>newArrayList(2, 5);
        PropertiesBuilder maxDepthPropertyBuilder = new PropertiesBuilder().setName("maxDepth").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(depthRange);
        parameters.add(new Parameter(maxDepthPropertyBuilder.createProperties()));

        List<Object> skipRange = Lists.<Object>newArrayList(0.6, 0.8, 0.9, 0.95, 0.98);
        PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(skipRange);
        parameters.add(new Parameter(ignoreAttrPropertyBuilder.createProperties()));

        List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 40);
        PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName("numTrees").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.0).setRange(numTreesRange);
        parameters.add(new Parameter(numTreesPropertyBuilder.createProperties()));

        List<Object> minimumScoreRange = Lists.<Object>newArrayList(Double.MIN_VALUE, 0.0, 0.00001, 0.0001, 0.001, 0.01, 0.1);
        PropertiesBuilder minimumScorePropertiesBuilder = new PropertiesBuilder().setName("minimumScore").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(false).setInitialGuessOfOptimalValue(0.001).setIsOrdinal(true).setRange(minimumScoreRange);
        parameters.add(new Parameter(minimumScorePropertiesBuilder.createProperties()));

        return parameters;
    }

    @Override
    public HashMap<String, Object> createPredictiveModelConfig(List<Parameter> parameters)  {
        HashMap<String, Object> predictiveModelConfig = new HashMap<String, Object>();
        for (Parameter parameter : parameters)
            predictiveModelConfig.put(parameter.properties.name, parameter.properties.optimalValue);
        if (!predictiveModelConfig.containsKey("maxDepth"))
            predictiveModelConfig.put("maxDepth", 4);
        if (!predictiveModelConfig.containsKey("ignoreAttributeAtNodeProbability"))
            predictiveModelConfig.put("ignoreAttributeAtNodeProbability", 0.7);
        if (!predictiveModelConfig.containsKey("numTrees"))
            predictiveModelConfig.put("numTrees", 4);
        if (!predictiveModelConfig.containsKey("minimumScore"))
            predictiveModelConfig.put("minimumScore", 0.0);
        return predictiveModelConfig;
    }

    @Override
    public RandomForestBuilder buildBuilder(Map<String, Object> parameters){
        TreeBuilder treeBuilder = new TreeBuilder()
                .maxDepth((Integer)parameters.get("maxDepth"))
                .ignoreAttributeAtNodeProbability((Double)parameters.get("ignoreAttributeAtNodeProbability"))
                .minimumScore((Double) parameters.get("minimumScore"));
        return new RandomForestBuilder(treeBuilder).numTrees((Integer)parameters.get("numTrees"));
    }
}
