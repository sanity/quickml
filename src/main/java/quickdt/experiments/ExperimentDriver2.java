package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.AbstractInstance;
import quickdt.Instance;
import quickdt.PredictiveModelOptimizer.*;
import quickdt.TreeBuilder;
import quickdt.experiments.crossValidation.CrossValidator;
import quickdt.experiments.crossValidation.RMSECrossValLoss;
import quickdt.randomForest.RandomForest;
import quickdt.randomForest.RandomForestBuilder;

import java.util.List;
import java.util.Map;

public class ExperimentDriver2 {
    public static void main(String[] args) {

        String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id"};
        TrainingDataGenerator2 trainingDataGenerator = new TrainingDataGenerator2(10000, .005, bidRequestAttributes);
        List<Instance> trainingData = trainingDataGenerator.createTrainingData();
        List<Parameter> parameters = Lists.newArrayList();
        List<Object> depthRange = Lists.newArrayList();
        depthRange.add(new Integer(1));
        depthRange.add(new Integer(2));
        depthRange.add(new Integer(3));
        PropertiesBuilder toAdd = new PropertiesBuilder().setName("maxDepth").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                .setParameterTolerance(1).setErrorTolerance(.05).setRange(depthRange);
        parameters.add(new Parameter(toAdd.createProperties()));
        List<Object> skipRange = Lists.newArrayList();
        skipRange.add(new Double(0.3));
        skipRange.add(new Double(0.7));
        toAdd = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                .setParameterTolerance(0.39).setErrorTolerance(.05).setRange(skipRange);
        parameters.add(new Parameter(toAdd.createProperties()));
        PredictiveModelOptimizer optimizer = new PredictiveModelOptimizer("exp",parameters, new RandomForestBuilderBuilder(), new CrossValidator(3), trainingData);
        Map<String, Object> optimalParameters = optimizer.findOptimalParameters();
        for (String key : optimalParameters.keySet())
            System.out.println(key + " " + optimalParameters.get(key));
    }

}