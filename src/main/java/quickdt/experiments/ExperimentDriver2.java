package quickdt.experiments;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.AbstractInstance;
import quickdt.calibratedPredictiveModel2.PAVCalibratedPredictiveModelBuilderBuilder;
import quickdt.experiments.crossValidation.CrossValidator;
import quickdt.predictiveModelOptimizer2.*;

import java.util.List;
import java.util.Map;

public class ExperimentDriver2 {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentDriver2.class);

    public static void main(String[] args) {
        String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id", "att 4", "att 5"};
        TrainingDataGenerator2 trainingDataGenerator = new TrainingDataGenerator2(1000, .4, bidRequestAttributes);
        List<AbstractInstance> trainingData = trainingDataGenerator.createTrainingData();
        List<Parameter> parameters = Lists.newArrayList();
        List<Object> depthRange = Lists.<Object>newArrayList(2, 4);
        PropertiesBuilder maxDepthPropertyBuilder = new PropertiesBuilder().setName("maxDepth").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(4).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(depthRange);
        parameters.add(new Parameter(maxDepthPropertyBuilder.createProperties()));

        List<Object> skipRange = Lists.<Object>newArrayList(0.3, 0.7);
        PropertiesBuilder ignoreAttrPropertyBuilder = new PropertiesBuilder().setName("ignoreAttributeAtNodeProbability").setBinarySearchTheRange(false).setInitialGuessOfOptimalValue(0.3).setIsOrdinal(true)
                .setParameterTolerance(0).setErrorTolerance(.05).setRange(skipRange);
        parameters.add(new Parameter(ignoreAttrPropertyBuilder.createProperties()));

        List<Object> numTreesRange = Lists.<Object>newArrayList(5, 10, 20, 40);
        PropertiesBuilder numTreesPropertyBuilder = new PropertiesBuilder().setName("numTrees").setBinarySearchTheRange(true).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(20).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.02).setRange(numTreesRange);
        parameters.add(new Parameter(numTreesPropertyBuilder.createProperties()));

        List<Object> binsInCalibratorRange = Lists.<Object>newArrayList(5, 10, 20, 40, 100, 200);
        PropertiesBuilder binsInCalibratorPropertyBuilder = new PropertiesBuilder().setName("binsInCalibrator").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(false).setInitialGuessOfOptimalValue(5).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.01).setRange(binsInCalibratorRange);
        parameters.add(new Parameter(binsInCalibratorPropertyBuilder.createProperties()));

        BestOptimum bestOptimum = new BestOptimum(3, new CrossValidator(3), parameters, new PAVCalibratedPredictiveModelBuilderBuilder(), trainingData);
        Map<String, Object> optimalParameters =  bestOptimum.findBestOptimum();

        logger.info(String.format("Optimal Parameters"));
        logger.info("Optimal parameters");
        for (String key : optimalParameters.keySet())
            logger.info(key + " " +optimalParameters.get(key));

    }

}