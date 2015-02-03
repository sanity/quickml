package quickml.supervised.alternative.crossValidationLoss;

import static quickml.supervised.alternative.crossValidationLoss.LossFunctions.rmseLoss;

public class ClassifierRMSELossFunction implements ClassifierLossFunction {

    @Override
    public double getLoss(PredictionMapResults results) {
        return rmseLoss(results);
    }

    @Override
    public String getName() {
        return "RMSE";
    }
}
