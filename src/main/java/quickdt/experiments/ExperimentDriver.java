package quickdt.experiments;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class ExperimentDriver {
    public static void main(String[] args) {
        ProbDistOfSumOfWeightedRandVars x = new ProbDistOfSumOfWeightedRandVars(100000, 5, 20, 20, 20, .005, 35);
        x.getAverageDeviationInPredictedProbabilities(400, 0.0015, true);


        //ProbDistOfSumOfIndepRandVars x = new ProbDistOfSumOfIndepRandVars(100000, 5, 10, 5, 20, .005, 16);
        //x.getAverageDeviationInPredictedProbabilities(400, 0.0015, true);
        //get prob for each .
    }

}
