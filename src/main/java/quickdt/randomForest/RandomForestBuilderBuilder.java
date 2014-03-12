package quickdt.randomForest;
import com.google.common.collect.Lists;
import quickdt.PredictiveModel;
import quickdt.PredictiveModelBuilder;
import quickdt.PredictiveModelBuilderBuilder;
import quickdt.PredictiveModelOptimizer.Parameter;
import quickdt.PredictiveModelOptimizer.PropertiesBuilder;
import quickdt.TreeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Object> skipRange = Lists.<Object>newArrayList(0.3, 0.7);
        PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(skipRange);
        parameters.add(new Parameter(ignoreAttrPropertyBuilder.createProperties()));

        List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 40);
        PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName("numTrees").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.0).setRange(numTreesRange);
        parameters.add(new Parameter(numTreesPropertyBuilder.createProperties()));

        return parameters;
    }

    @Override
    public HashMap<String, Object> createPredictiveModelConfig(List<Parameter> parameters)  {
        HashMap<String, Object> predictiveModelConfig = new HashMap<String, Object>();
        for (Parameter parameter : parameters)
            predictiveModelConfig.put(parameter.properties.name, parameter.properties.optimalValue);
        if (!predictiveModelConfig.containsKey("maxDepth"))
            predictiveModelConfig.put("maxDepth", new Integer(4));
        if (!predictiveModelConfig.containsKey("ignoreAttributeAtNodeProbability"))
            predictiveModelConfig.put("ignoreAttributeAtNodeProbability", new Double(0.7));
        if (!predictiveModelConfig.containsKey("numTrees"))
            predictiveModelConfig.put("numTrees", new Integer(4));
        return predictiveModelConfig;
    }

    @Override
    public RandomForestBuilder buildBuilder(Map<String, Object> parameters){
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth((Integer)parameters.get("maxDepth")).ignoreAttributeAtNodeProbability((Double)parameters.get("ignoreAttributeAtNodeProbability"));
        return new RandomForestBuilder(treeBuilder).numTrees((Integer)parameters.get("numTrees"));
    }
}
