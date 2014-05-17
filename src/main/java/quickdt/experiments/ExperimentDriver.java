package quickdt.experiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.*;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.util.List;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class ExperimentDriver {
    private static final Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);


    public static void main(String[] args) {
        int numTraniningExamples = 40000;
        String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id", "favorite_soccer_team_id", "user_iq"};
        TrainingDataGenerator2 trainingDataGenerator = new TrainingDataGenerator2(numTraniningExamples, .005, bidRequestAttributes);
        List<AbstractInstance> trainingData = trainingDataGenerator.createTrainingData();
        int minuteInMillis = 60000;
        int instanceNumber = 0;
        for (AbstractInstance instance : trainingData) {
            instance.getAttributes().put("currentTimeMillis", minuteInMillis * instanceNumber);
            instanceNumber++;
        }
        logger.info("trainingDataSize " + trainingData.size());
        RandomForestBuilder randomForestBuilder = getRandomForestBuilder(5, 5);
//       randomForestBuilder.buildPredictiveModel(trainingData);
//       CrossValidator crossValidator = new StationaryCrossValidator(4,3,new aucCrossValLoss());
//       should it be number of time slices or minutes in a time slice? total minutes in a time slice
//       number in minute.  Or should the number of samples be the invariant?  this is good on a number of levels.
// First, need a min number of samples to eliminate the noise of the cross validation set? No..not really cuz we are averaging the loss
        //in practice the time between checks will be the invariant.  But, yes,
        // //the weight of the cv should be factored into the running average

         CrossValidator crossValidator = new OutOfTimeCrossValidator(new LogCrossValLoss(), 0.25, 30, new TestDateTimeExtractor()); //number of validation time slices

        double totalLoss = crossValidator.getCrossValidatedLoss(randomForestBuilder, trainingData);
        logger.info("total loss " + totalLoss);
    }
      /*  ProbDistOfSumOfIndepRandVars x = new ProbDistOfSumOfIndepRandVars(100000, 5, 20, 2, 5, .005, 16);
        x.getAverageDeviationInPredictedProbabilities(400, 0.0015, true);


      //  ProbDistOfVarMultivariateGaussian x = new ProbDistOfVarMultivariateGaussian(400000, 3, 5, 4, 3, 4, 5, 1.5, 100000, .5, 0.5);
      //  x.getAverageDeviationInPredictedProbabilities(100, 0.0001, true);
      String bidRequestAttributes[] = {"seller_id", "user_id", "users_favorite_beer_id", "favorite_soccer_team_id", "user_iq"};
      TrainingDataGenerator trainingDataGenerator = new TrainingDataGenerator(600000, .005, bidRequestAttributes);
      List<Instance> trainingData = trainingDataGenerator.createTrainingData();
      RandomForest randomForest = getRandomForest(trainingData, 5, 5);
        System.out.println("here3");

      trainingDataGenerator.getAverageDeviationInPredictedProbabilities(400, .0013, randomForest);


      //  ProbDistOfVarDependentOnCatVars x = new ProbDistOfVarDependentOnCatVars(400000, 5, 5, 10, 10, 3, .005, 3);
      //  x.getAverageDeviationInPredictedProbabilities(400, 0.0001, true);

        //ProbDistOfSumOfWeightedRandVars x = new ProbDistOfSumOfWeightedRandVars(100000, 5, 20, 20, 20, .005, 35);
        //x.getAverageDeviationInPredictedProbabilities(400, 0.0015, true);



        //get prob for each .
    }
   */
    private static RandomForest getRandomForest(List<AbstractInstance> trainingData, int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }
    private static RandomForestBuilder getRandomForestBuilder(int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        return randomForestBuilder;
    }

}
