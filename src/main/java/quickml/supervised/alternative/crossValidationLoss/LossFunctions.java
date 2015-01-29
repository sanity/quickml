package quickml.supervised.alternative.crossValidationLoss;

import com.google.common.base.Preconditions;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;

import java.io.Serializable;
import java.util.List;

public class LossFunctions {

    public static double mseLoss(PredictionMapResults results) {
        int totalLoss = 0;
        for (PredictionMapResults.PredictionMapResult result : results.getResults()) {
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
