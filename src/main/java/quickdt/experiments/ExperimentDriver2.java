package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.Instance;
import quickdt.predictiveModelOptimizer.*;
import quickdt.experiments.crossValidation.CrossValidator;

import java.util.List;
import java.util.Map;

public class ExperimentDriver2 {
    public static void main(String[] args) {

        String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id"};
        TrainingDataGenerator2 trainingDataGenerator = new TrainingDataGenerator2(10000, .005, bidRequestAttributes);
        List<Instance> trainingData = trainingDataGenerator.createTrainingData();
        List<Parameter> parameters = Lists.newArrayList();
        List<Object> depthRange = Lists.<Object>newArrayList(2, 4, 6);
        PropertiesBuilder maxDepthPropertyBuilder = new PropertiesBuilder().setName("maxDepth").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                .setParameterTolerance(1).setErrorTolerance(.05).setRange(depthRange);
        parameters.add(new Parameter(maxDepthPropertyBuilder.createProperties()));
        List<Object> skipRange = Lists.<Object>newArrayList(0.1, 0.3, 0.5, 0.7, 0.9);
       PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                .setParameterTolerance(0.39).setErrorTolerance(.05).setRange(skipRange);
        parameters.add(new Parameter(ignoreAttrPropertyBuilder.createProperties()));
        List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 50, 100);
        PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName("numTrees").setBinarySearchTheRange(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(.05).setRange(numTreesRange);
        parameters.add(new Parameter(numTreesPropertyBuilder.createProperties()));
        PredictiveModelOptimizer optimizer = new PredictiveModelOptimizer("exp",parameters, new RandomForestBuilderBuilder(), new CrossValidator(3), trainingData);
        Map<String, Object> optimalParameters = optimizer.findOptimalParameters();
        System.out.println("Optimal parameters");
        for (String key : optimalParameters.keySet())
            System.out.println(key + " " + optimalParameters.get(key));
    }

}