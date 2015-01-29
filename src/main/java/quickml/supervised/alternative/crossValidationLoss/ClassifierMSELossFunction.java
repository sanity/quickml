package quickml.supervised.alternative.crossValidationLoss;

import static quickml.supervised.alternative.crossValidationLoss.LossFunctions.mseLoss;

public class ClassifierMSELossFunction implements ClassifierLossFunction {

    @Override
    public double getLoss(PredictionMapResults results) {
        return mseLoss(results);
    }
}
