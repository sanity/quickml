package quickdt.experiments;

import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class ExperimentDriver {
    public static void main(String[] args) {
        ProbDistOfSumOfIndepRandVars x = new ProbDistOfSumOfIndepRandVars(100000, 5, 20, 2, 5, .005, 16);
        x.getAverageDeviationInPredictedProbabilities(400, 0.0015, true);

/*

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
    private static RandomForest getRandomForest(List<Instance> trainingData, int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        return randomForestBuilder.buildPredictiveModel(trainingData);
*/    }

}
