package quickml.supervised.crossValidation.lossfunctions;

import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;

import java.util.List;

public class LossFunctions {

    public static double mseClassifierLoss(PredictionMapResults results) {
        double totalLoss = 0;
        for (PredictionMapResult result : results) {
            final double error = (1.0 - result.getPredictionForLabel());
            final double errorSquared = error * error * result.getWeight();
            totalLoss += errorSquared;
        }
        return results.totalWeight() > 0 ? totalLoss / results.totalWeight() : 0;
    }

    public static double rmseClassifierLoss(PredictionMapResults results) {
        return Math.sqrt(mseClassifierLoss(results));
    }

    public static double mseRegressionLoss(List<LabelPredictionWeight<Double, Double>> results) {
        double totalLoss = 0;
        double totalWeight = 0;
        for (LabelPredictionWeight<Double, Double> result : results) {
            final double error = (result.getLabel() - result.getPrediction());
            final double errorSquared = error * error * result.getWeight();
            totalLoss += errorSquared*result.getWeight();
            totalWeight += result.getWeight();
        }
        return totalWeight > 0 ? totalLoss / totalWeight : 0;
    }

    public static double rmseRegressionLoss(List<LabelPredictionWeight<Double, Double>> results) {
        return Math.sqrt(mseRegressionLoss(results));
    }



}
