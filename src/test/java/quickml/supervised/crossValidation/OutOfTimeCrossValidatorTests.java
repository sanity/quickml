package quickml.supervised.crossValidation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.MapWithDefaultOfZero;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.NonWeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.TestDateTimeExtractor;
import quickml.data.Instance;
import quickml.experiments.TrainingDataGenerator2;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class OutOfTimeCrossValidatorTests {
    private static final Logger logger = LoggerFactory.getLogger(OutOfTimeCrossValidator.class);
    List<Instance<Map<String, Serializable>>> trainingData;

    @Before
    public void setUp() throws Exception {
        int numTraniningExamples = 40100;
        String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id", "favorite_soccer_team_id", "user_iq"};
        TrainingDataGenerator2 trainingDataGenerator = new TrainingDataGenerator2(numTraniningExamples, .005, bidRequestAttributes);
        trainingData = trainingDataGenerator.createTrainingData();
        int millisInMinute = 60000;
        int instanceNumber = 0;
        for (Instance<Map<String, Serializable>> instance : trainingData) {
            instance.getRegressors().put("currentTimeMillis", millisInMinute * instanceNumber);
            instanceNumber++;
        }
    }

    @Test
    public void testLossBetween0And1() {
        //  List<Instance<Map<String, Serializable>>> trainingData = setUp();
        logger.info("trainingDataSize " + trainingData.size());
        PredictiveModelWithDataBuilder<Map<String, Serializable>, ? extends PredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero>> predictiveModelWithDataBuilder = getPredictiveModelWithDataBuilder(5, 5);

        ClassifierOutOfTimeCrossValidator crossValidator = new ClassifierOutOfTimeCrossValidator(new ClassifierLogCVLossFunction(), 0.25, 30, new TestDateTimeExtractor()); //number of validation time slices
        double totalLoss = crossValidator.getCrossValidatedLoss(predictiveModelWithDataBuilder, trainingData);
        Assert.assertTrue(totalLoss > 0 && totalLoss <= 1.0);
        logger.info("\n\nAUCLoss\n");
        crossValidator = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.25, 30, new TestDateTimeExtractor()); //number of validation time slices
        totalLoss = crossValidator.getCrossValidatedLoss(predictiveModelWithDataBuilder, trainingData);
        logger.info("totalLoss from AUc "+ totalLoss);
        Assert.assertTrue(totalLoss > 0 && totalLoss <= 1.0);
//
        // crossValidator = new OutOfTimeCrossValidator(new ClassifierRMSECrossValLossFunction(), 0.25, 30, new TestDateTimeExtractor()); //number of validation time slices
        // totalLoss = crossValidator.getCrossValidatedLoss(randomForestBuilder, trainingData);
        // Assert.assertTrue(totalLoss > 0 && totalLoss <=1.0);
    }


    private static RandomForest getRandomForest(List<Instance<Map<String, Serializable>>> trainingData, int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }

    private static PredictiveModelWithDataBuilder<Map<String, Serializable>, ? extends PredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero>> getPredictiveModelWithDataBuilder(int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        PredictiveModelWithDataBuilder<Map<String, Serializable>, ? extends PredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero>> builder = new PredictiveModelWithDataBuilder<>(randomForestBuilder);//.rebuildThreshold(4).splitNodeThreshold(2);
        return builder;
    }
}
