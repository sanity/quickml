package quickdt.experiments;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.calibratedPredictiveModel.PAVCalibratedPredictiveModelBuilderBuilder;
import quickdt.experiments.crossValidation.CrossValidator;
import quickdt.predictiveModelOptimizer.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictiveModelOptimizerDriver {
    private static final Logger logger = LoggerFactory.getLogger(PredictiveModelOptimizerDriver.class);

    public static void main(String[] args) {
        String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id", "att 4", "att 5"};
        TrainingDataGenerator2 trainingDataGenerator = new TrainingDataGenerator2(1000, .4, bidRequestAttributes);
        List<AbstractInstance> trainingData = trainingDataGenerator.createTrainingData();
        List<ParameterToOptimize> parametersToOptimize = Lists.newArrayList();
        List<Object> depthRange = Lists.<Object>newArrayList(2, 4);
        PropertiesBuilder maxDepthPropertyBuilder = new PropertiesBuilder().setName("maxDepth").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(depthRange);
        parametersToOptimize.add(new ParameterToOptimize(maxDepthPropertyBuilder.createProperties()));

        List<Object> skipRange = Lists.<Object>newArrayList(0.8, 0.9);
        PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.8).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(skipRange);
        parametersToOptimize.add(new ParameterToOptimize(ignoreAttrPropertyBuilder.createProperties()));
        Map<String, Object> initialPredictiveModelParameters = new HashMap<String, Object>();
        initialPredictiveModelParameters.put("binsInCalibrator", new Integer(10));
        initialPredictiveModelParameters.put("numTrees", new Integer(16));
        initialPredictiveModelParameters.put("executorThreadCount", new Integer(4));


/*
        List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 40);
        PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName("numTrees").setBinarySearchTheRange(true).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.00).setRange(numTreesRange);
        parametersToOptimize.add(new ParameterToOptimize(numTreesPropertyBuilder.createProperties()));

        List<Object> binsInCalibratorRange = Lists.<Object>newArrayList(5, 10, 20, 40, 100, 200);
        PropertiesBuilder binsInCalibratorPropertyBuilder = new PropertiesBuilder().setName("binsInCalibrator").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(false).setInitialGuessOfOptimalValue(5).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.01).setRange(binsInCalibratorRange);
        parametersToOptimize.add(new ParameterToOptimize(binsInCalibratorPropertyBuilder.createProperties()));

*/
        PAVCalibratedPredictiveModelBuilderBuilder pavCalibratedPredictiveModelBuilderBuilder = new PAVCalibratedPredictiveModelBuilderBuilder(initialPredictiveModelParameters);
        BestOptimum bestOptimum = new BestOptimum(1, 1,  new CrossValidator(4,2), parametersToOptimize, pavCalibratedPredictiveModelBuilderBuilder, trainingData);
        Map<String, Object> optimalParameters =  bestOptimum.findBestOptimum();

        logger.info("parametersToOptimize");
        for (String key : optimalParameters.keySet())
            logger.info(key + " " +optimalParameters.get(key));

    }

}