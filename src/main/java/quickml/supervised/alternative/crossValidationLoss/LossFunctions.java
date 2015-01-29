package quickml.supervised.alternative.crossValidationLoss;

public class LossFunctions {

    public static double mseLoss(PredictionMapResults results) {
        int totalLoss = 0;
        for (PredictionMapResult result : results) {
            final double error = (1.0 - result.getPredictionForLabel());
            final double errorSquared = error * error * result.getWeight();
            totalLoss += errorSquared;
        }
        return results.totalWeight() > 0 ? totalLoss / results.totalWeight() : 0;
    }

    public static double rmseLoss(PredictionMapResults results) {
        return Math.sqrt(mseLoss(results));
    }

}
